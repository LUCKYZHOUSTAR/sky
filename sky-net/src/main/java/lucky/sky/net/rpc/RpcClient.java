package lucky.sky.net.rpc;

import lucky.sky.net.rpc.config.ClientOptions;
import lucky.sky.net.rpc.config.RpcClientConfig;
import lucky.sky.net.rpc.registry.LookupInfo;
import lucky.sky.net.rpc.registry.Provider;
import lucky.sky.net.rpc.registry.Registry;
import lucky.sky.net.rpc.registry.ZkRegistry;
import lucky.sky.net.rpc.simple.client.SimpleInvokerFactory;
import lucky.sky.net.rpc.simple.client.future.InvokeFuture;
import lucky.sky.net.rpc.simple.data.SimpleResponseMessage;
import lucky.sky.util.config.SettingMap;
import lucky.sky.util.lang.FaultException;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public final class RpcClient {

  private static final ConcurrentHashMap<String, Object> proxies = new ConcurrentHashMap<>();

  private RpcClient() {
    // 防止实例化
  }

  /**
   * 获取服务代理对象
   */
  @SuppressWarnings("unchecked")
  public static <T> T get(String server, Class<T> clazz) {
    ServiceMeta meta = ServiceMetaFactory.get(clazz);
    String key = server + "." + meta.getServiceName();
    Object obj = proxies.get(key);
    if (obj != null) {
      return (T) obj;
    }

    InvokerContainer container = InvokerContainer.get(server);
    T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
        new RpcInvoker(meta, container));
    proxies.put(key, proxy);
    return proxy;
  }

  /**
   * 获取服务代理对象
   */
  @SuppressWarnings("unchecked")
  public static <T> T get(String server, String service, Class<T> clazz) {
    String key = server + "." + service;
    Object obj = proxies.get(key);
    if (obj != null) {
      return (T) obj;
    }

    InvokerContainer container = InvokerContainer.get(server);
    ServiceMeta meta = ServiceMetaFactory.get(clazz);
    T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
        new RpcInvoker(meta, container, service));
    proxies.put(key, proxy);
    return proxy;
  }

  /**
   * 服务代理执行器
   */
  static class RpcInvoker implements InvocationHandler {

    private static Logger logger = LoggerManager.getLogger(RpcInvoker.class);
    private InvokerContainer container;
    private String serviceName;
    private Map<String, String> methodNames;

    public RpcInvoker(ServiceMeta meta, InvokerContainer container) {
      this(meta, container, null);
    }

    public RpcInvoker(ServiceMeta meta, InvokerContainer container, String serviceName) {
      this.container = container;
      this.serviceName = (serviceName == null) ? meta.getServiceName() : serviceName;
      this.methodNames = meta.getMethodNames();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (Object.class == method.getDeclaringClass()) {
        return method.invoke(this, args);
      }

      String methodName = methodNames.get(method.getName());
      if (methodName == null) {
        return method.invoke(this, args);
      }

      return invokeRemote(args, method, methodName);
    }

    private Object invokeRemote(Object[] args, Method method, String methodName) {
      // 后续考虑支持其它负载均衡策略
      List<Invoker> invokers = container.getInvokers();
      List<FaultException> nodeFaults;

      // 先尝试随机来一个节点
      int index = new Random().nextInt(invokers.size());
      try {
        InvokeFuture future = invokers.get(index)
            .invoke(serviceName, methodName, args, method.getReturnType());
        return toResult(future);
      } catch (FaultException e) {
        if (invokers.size() == 1) {
          // 仅一个节点，无需重试
          throw e;
        }

        processError(e);
        nodeFaults = new ArrayList<>(invokers.size());
        nodeFaults.add(e);
      }

      // 如果失败则依次调用其它节点
      for (int i = 0; i < invokers.size(); i++) {
        if (i == index) {
          continue;
        }

        try {
          InvokeFuture future = invokers.get(i)
              .invoke(serviceName, methodName, args, method.getReturnType());
          return toResult(future);
        } catch (FaultException e) {
          processError(e);
          nodeFaults.add(e);
        }
      }

      FaultException allNodesFailedFault = RpcError.CLIENT_ALL_NODES_FAILED.toFault();
      RpcExceptions.putNodeFaults(allNodesFailedFault, nodeFaults);
      throw allNodesFailedFault;
    }


    public Object toResult(InvokeFuture future) {
      try {
        SimpleResponseMessage responseMessage = (SimpleResponseMessage) future.getResult();
        return responseMessage.getResult();
      } catch (Throwable e) {
        if (e instanceof FaultException) {
          throw (FaultException) e;
        }
        throw new FaultException(RpcError.CLIENT_UNKNOWN_ERROR.value(),
            RpcError.CLIENT_UNKNOWN_ERROR.getMessage());
      }
    }

    private static void processError(FaultException e) {
      // 后续考虑修改成 Invoker 实现类中只抛出 RpcException, 然后在此类中再转换成 FaultException
      int errorCode = e.getErrorCode();
      if (RpcError.isBusinessError(errorCode)) {
        throw e;
      }
      logger.warn("invoke failed: ", e);
    }
  }

  /**
   * 执行器容器
   */
  static class InvokerContainer {

    private static final Logger log = LoggerManager.getLogger(InvokerContainer.class);
    private static final Registry registry = ZkRegistry.INSTANCE;
    private static final ConcurrentHashMap<String, InvokerContainer> containers = new ConcurrentHashMap<>();
    private String server;
    private List<Invoker> invokers;
    private boolean initialized;

    private InvokerContainer(String server) {
      this.server = server;
    }

    public static synchronized InvokerContainer get(String server) {
      InvokerContainer container = containers.get(server);
      if (container == null) {
        container = new InvokerContainer(server);
        containers.put(server, container);
      }
      return container;
    }

    public List<Invoker> getInvokers() {
      if (!this.initialized) {
        synchronized (this) {
          if (!this.initialized) {
            this.initialize();
          }
        }
      }
      return invokers;
    }

    private void setInvokers(List<Invoker> invokers) {
      this.invokers = invokers;
    }

    private void initialize() {
      ClientOptions options = RpcClientConfig.get(server);

      // 从注册中心获取节点
      List<Invoker> list = initializeFromRegistry(options);

      // 尝试直连
      if (list.isEmpty()) {
        if (options == null) {
          log.error("尝试直连，但没找到名为 {} 的服务配置", server);
          throw new RpcException(RpcError.CLIENT_NO_PROVIDER, server);
        } else {
          log.info("{}直连服务 {} {} ", options.isDiscovery() ? "尝试" : "", server,
              options.getAddress());
          Invoker invoker = createInvoker(options);
          list.add(invoker);
        }
      }

      this.invokers = list;
      this.initialized = true;
    }

    // 从注册中心获取节点
    private List<Invoker> initializeFromRegistry(ClientOptions options) {
      List<Invoker> list = new ArrayList<>();

      if (options == null || options.isDiscovery()) {
        LookupInfo info = new LookupInfo(server, options == null ? null : options.getAlias());
        info.setGroup(options == null ? null : options.getGroup());
        info.setVersion(options == null ? null : options.getVersion());
        info.setConsumer(InvokerContainer::updateProviders);
        List<Provider> providers = registry.lookup(info);
        if (providers != null && !providers.isEmpty()) {
          providers.forEach(provider -> {
            Invoker invoker = createInvoker(getOptions(provider, options));
            list.add(invoker);
          });
        } else {
          log.warn("从注册中心未获取任何属于服务 {} 的节点", server);
        }
      }

      return list;
    }

    /**
     * 刷新节点
     *
     * @param server 服务程序名称
     * @param providers 节点信息
     */
    private static void updateProviders(String server, List<Provider> providers) {
      ClientOptions options = RpcClientConfig.get(server);
      List<Invoker> invokers = new ArrayList<>();
      providers.forEach(provider -> {
        Invoker invoker = createInvoker(getOptions(provider, options));
        invokers.add(invoker);
      });

      InvokerContainer container = containers.get(server);
      if (container != null) {
        container.setInvokers(invokers);
      }
    }

    /**
     * 合并参数
     */
    private static ClientOptions getOptions(Provider provider, ClientOptions options) {
      ClientOptions clientOptions = new ClientOptions(provider);
      if (options != null) {
        clientOptions.setGroup(options.getGroup());
        clientOptions.setVersion(options.getVersion());
        SettingMap settings = clientOptions.getSettings();
        options.getSettings().each(settings::put);
      }
      return clientOptions;
    }

    /**
     * 创建远程执行器
     */
    private static Invoker createInvoker(ClientOptions options) {
      if ("simple".equals(options.getType())) {
        return SimpleInvokerFactory.get(options);
      }
      throw new RpcException(RpcError.CLIENT_INVALID_SERVER_TYPE, options.getName(),
          options.getType());
    }
  }

}
