package lucky.sky.task;

import lucky.sky.net.rpc.config.RpcServerConfig;
import lucky.sky.net.rpc.config.ServerOptions;
import lucky.sky.util.boot.Application;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.lang.StrKit;

import java.util.Map;

/**
 *
 */
public class TaskApplication extends Application {

  private boolean autoScan = true;
  private TaskServer server;

  public TaskApplication(Class<?> bootClass, String[] args) {
    super(bootClass, args);
    server = new TaskServer();
  }

  public TaskApplication(Class<?> bootClass, String[] args, ServerOptions defaultOptions) {
    super(bootClass, args);
    String appName = AppConfig.getDefault().getAppName();
    defaultOptions.cover(RpcServerConfig.get(appName));
    this.server = new TaskServer(defaultOptions);
  }

  public void setAutoScan(boolean autoScan) {
    this.autoScan = autoScan;
  }

  @Override
  protected void initDefaultProperties(Map<String, Object> props) {
    super.initDefaultProperties(props);
    props.put("spring.main.web_environment", false);
  }

  /**
   * 注册执行器
   *
   * @param name 任务名称
   * @param executor 执行器
   */
  public void registerTask(String name, Executor executor) {
    server.register(name, executor);
    logger.info("register task: {} -> {}", name, executor.getClass().getName());
  }

  private void scanTasks() {
    Map<String, Executor> beans = ctx.getBeansOfType(Executor.class);
    logger.info("find {} tasks", beans.size());
    beans.forEach((k, executor) -> {
      Class<?> clazz = executor.getClass();
      Task task = clazz.getAnnotation(Task.class);
      String name =
          (task == null || StrKit.isBlank(task.name())) ? clazz.getSimpleName() : task.name();
      this.registerTask(name, executor);
    });
  }

  @Override
  protected void load() {
    if (autoScan) {
      scanTasks();
    }
    server.start();
  }
}
