package lucky.sky.task;

import lucky.sky.net.rpc.RpcServer;
import lucky.sky.net.rpc.config.ServerOptions;
import lucky.sky.task.service.TaskService;
import lucky.sky.task.service.TaskServiceImp;
import lucky.sky.util.config.AppConfig;

import java.util.HashMap;

/**
 * 任务执行器服务
 */
public class TaskServer {

  private RpcServer server;
  private HashMap<String, Executor> executors = new HashMap<>();

  public TaskServer() {
    String name = AppConfig.getDefault().getAppName();
    server = new RpcServer(name);
  }

  public TaskServer(ServerOptions options) {
    server = new RpcServer(options);
  }

  /**
   * 启动服务
   */
  public void start() {
    server.registerService(TaskService.class, new TaskServiceImp(executors));
    server.start();
  }

  /**
   * 注册执行器
   *
   * @param name 任务名称
   * @param executor 执行器
   */
  public void register(String name, Executor executor) {
    executors.put(name, executor);
  }
}
