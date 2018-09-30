package lucky.sky.net.rpc;

import lucky.sky.net.rpc.annotation.RpcMethod;
import lucky.sky.net.rpc.annotation.RpcService;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import lucky.sky.util.reflect.MethodAccess;

public class ServiceContainer {

  private static final Logger logger = LoggerManager.getLogger(ServiceContainer.class);
  protected HashMap<String, MethodExecutor> executors = new HashMap<>();
  private Map<String, ServiceInfo> services = new TreeMap<>();

  public void registerService(Object instance) {
    Class<?> clazz = instance.getClass();
    this.registerService(clazz, instance);
  }

  public void registerService(String name, Object instance) {
    Class<?> clazz = instance.getClass();
    RpcService rpcService = clazz.getAnnotation(RpcService.class);
    String description = rpcService == null ? "" : rpcService.description();
    NamingConvention convention =
        rpcService == null ? NamingConvention.PASCAL : rpcService.convention();
    this.registerService(clazz, instance, name, description, convention);
  }

  public void registerService(Class<?> clazz, Object instance) {
    RpcService rpcService = clazz.getAnnotation(RpcService.class);
    String description = rpcService == null ? "" : rpcService.description();
    NamingConvention convention =
        rpcService == null ? NamingConvention.PASCAL : rpcService.convention();

    String name = null;
    if (rpcService != null) {
      name = rpcService.name();
    }
    if (StrKit.isBlank(name)) {
      name = clazz.getSimpleName();
    }

    this.registerService(clazz, instance, name, description, convention);
  }

  private void registerService(Class<?> clazz, Object instance, String name, String description,
      NamingConvention convention) {
    logger.info("register service: " + name);

    MethodAccess access = MethodAccess.get(clazz);
    Method[] methods = clazz.getMethods();
    for (Method m : methods) {
      if (m.getDeclaringClass() == Object.class) {
        continue;
      }

      try {
        int index = access.getIndex(m.getName());
        String methodName = getMethodName(m, convention);
        MethodExecutor mi = new MethodExecutor(instance, access, index);
        executors.put(buildKey(name, methodName), mi);
        logger.info("register service: {}.{}", name, methodName);
      } catch (IllegalArgumentException e) {
        logger.warn("find method index failed: {}", e);
      }
    }

    ServiceInfo serviceInfo = new ServiceInfo(clazz, name, description, convention);
    this.services.put(serviceInfo.getName(), serviceInfo);
  }

  public MethodExecutor getExecutor(String service, String method) {
    return executors.get(buildKey(service, method));
  }

  public Object execute(String service, String method, Object[] args) {
    MethodExecutor executor = executors.get(buildKey(service, method));
    if (executor == null) {
      throw new RpcException(RpcError.SERVER_SERVICE_NOT_FOUND, service, method);
    }
    return executor.invoke(args);
  }

  public Map<String, ServiceInfo> getServices() {
    return services;
  }

  private String buildKey(String serviceName, String methodName) {
    return serviceName + "." + methodName;
  }

  public static String getMethodName(Method method, NamingConvention convention) {
    RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
    String name = null;
    if (rpcMethod != null) {
      name = rpcMethod.name();
    }
    if (StrKit.isBlank(name)) {
      name = NamingConvention.transform(method.getName(), convention);
    }
    return name;
  }

  /**
   * 不区分方法名首字母大小写的服务容器, 用以兼容 soa-spring 来的请求, 待全部迁移后此类可移除
   */
  public static class IgnoreCaseServiceContainer extends ServiceContainer {

    @Override
    public MethodExecutor getExecutor(String service, String method) {
      MethodExecutor executor = super.getExecutor(service, method);
      if (executor == null && Character.isLowerCase(method.charAt(0))) {
        String pascalName = StrKit.firstCharToUpperCase(method);
        executor = super.getExecutor(service, pascalName);
      }
      return executor;
    }
  }
}
