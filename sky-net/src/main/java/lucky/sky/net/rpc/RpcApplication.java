package lucky.sky.net.rpc;

import lucky.sky.net.rpc.config.RpcServerConfig;
import lucky.sky.net.rpc.config.ServerOptions;
import lucky.sky.util.boot.Application;
import lucky.sky.util.boot.HttpMonitor;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.lang.Classes;
import lucky.sky.util.lang.StrKit;

import java.util.List;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("unchecked")
public class RpcApplication extends Application {

  private RpcServer server;

  /**
   * 启动 RPC 服务
   *
   * @param bootClass 应用程序启动类
   * @param args 启动参数
   */
  public RpcApplication(Class<?> bootClass, String[] args) {
    super(bootClass, args);
    reportEnabled = false;
    server = new RpcServer();
  }

  /**
   * 启动 RPC 服务
   *
   * @param bootClass 应用程序启动类
   * @param args 启动参数
   * @param defaultOptions 默认参数, 可在配置文件中进行覆盖
   */
  public RpcApplication(Class<?> bootClass, String[] args, ServerOptions defaultOptions) {
    super(bootClass, args);
    reportEnabled = false;
    if (defaultOptions == null) {
      server = new RpcServer();
    } else {
      String appName = AppConfig.getDefault().appName();
      defaultOptions.cover(RpcServerConfig.get(appName));
      server = new RpcServer(defaultOptions);
    }
  }

  /**
   * 注册服务
   *
   * @param clazz 服务接口
   * @param instance 服务实例
   */
  public void registerService(Class<?> clazz, Object instance) {
    server.registerService(clazz, instance);
  }

  @Override
  protected void initDefaultProperties(Map<String, Object> props) {
    super.initDefaultProperties(props);
    props.put("spring.main.web_environment", false);
  }

  private void scanServices() {
    scanServiceClass();
    scanServicePackage();
  }

  // 按类扫描, 支持扫描接口类和实现类
  private void scanServiceClass() {
    String serviceClass = server.getOptions().getSettings().getString("ServiceClass");
    if (StrKit.isBlank(serviceClass)) {
      return;
    }

    String[] classes = serviceClass.split(",");
    for (String className : classes) {
      try {
        Class clazz = Class.forName(className);
        Object instance = ctx.getBean(clazz);
        if (clazz.isInterface()) {
          server.registerService(clazz, instance);
        } else {
          server.registerService(instance);
        }
      } catch (Exception e) {
        logger.error("load class [{}] failed: {}", className, e);
      }
    }
  }

  // 按包扫描, 只扫描接口类
  private void scanServicePackage() {
    String[] packages;
    String servicePackage = server.getOptions().getSettings().getString("ServicePackage");
    if (StrKit.isBlank(servicePackage)) {
      packages = new String[]{bootClass.getPackage().getName() + ".iface"};
    } else {
      packages = servicePackage.split(",");
    }

    for (String pkg : packages) {
      List<String> classes = Classes.getClassListByPackage(pkg, false);
      classes.forEach(this::tryRegisterService);
    }
  }

  private void tryRegisterService(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      if (clazz.isInterface()) {
        Object instance = getBean(clazz);
        if (instance == null) {
          logger.warn("cannot find bean [{}]", className);
        } else {
          this.server.registerService(clazz, instance);
        }
      }
    } catch (Exception e) {
      logger.error("load class [{}] failed: {}", className, e);
    }
  }

  @Override
  protected void load() {
    scanServices();
    server.start();
  }

  @Override
  protected void initMonitor(HttpMonitor monitor) {
    monitor.registerHandler("/$stats", e -> {
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("<p>clients.active: %s</p>", server.getData("clients.active")));
      sb.append(String.format("<p>clients.max: %s</p>", server.getData("clients.max")));
      sb.append(String.format("<p>threads.active: %s</p>", server.getData("threads.active")));
      sb.append(String.format("<p>threads.max: %s</p>", server.getData("threads.max")));
      return sb.toString();
    });
  }
}
