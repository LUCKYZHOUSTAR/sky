package lucky.sky.task.service;

import lucky.sky.net.rpc.RpcClient;
import lucky.sky.task.Executor;
import lucky.sky.task.TaskContext;
import lucky.sky.task.data.ExecuteParam;
import lucky.sky.task.data.NotifyRequest;
import lucky.sky.task.data.Result;
import lucky.sky.util.encode.JsonEncoder;
import lucky.sky.util.lang.Exceptions;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import lucky.sky.util.net.IPUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class TaskServiceImp implements TaskService {

  private static Logger logger = LoggerManager.getLogger(TaskServiceImp.class);
  private Map<String, Executor> executors;
  private ExecutorService pool = Executors.newCachedThreadPool();
  private ScheduleService notifyService = RpcClient
      .get("sky.task.center.service", ScheduleService.class);
  private ConcurrentMap<String, String> runningTasks = new ConcurrentHashMap<>();

  public TaskServiceImp(Map<String, Executor> executors) {
    this.executors = executors;
  }

  /**
   * 执行任务
   *
   * @param param 任务参数
   */
  @Override
  public Result execute(ExecuteParam param) {
    logger.info("接收任务:{}", JsonEncoder.DEFAULT.encode(param));
    if (param.getType() == ExecuteParam.ExecuteType.AUTO && runningTasks
        .containsKey(param.getName())) {
      pool.execute(() -> {
        String error = String
            .format("任务 %s 正在执行, 跳过此次调度(如果多次发生类型情况, 请检查调度时间是否合理)", param.getName());
        logger.warn(error);
        LocalDateTime start = LocalDateTime.now();
        this.notify(param.getName(), param.getId(), new Result(false, error), start,
            LocalDateTime.now());
      });
      return new Result(true, null);
    }

    String name = StrKit.isBlank(param.getAlias()) ? param.getName() : param.getAlias();
    Executor executor = executors.get(name);
    if (executor == null) {
      logger.error("找不到任务:{}", name);
      return new Result(false, "找不到任务: " + name);
    }

    try {
      if (param.getType() == ExecuteParam.ExecuteType.AUTO) {
        runningTasks.put(param.getName(), param.getAlias());
      }
      pool.execute(() -> this.execute(executor, param));
    } catch (Exception e) {
      logger.error("提交任务到线程池失败", e);
      return new Result(false, "提交任务到线程池失败: " + e.getMessage());
    }

    return new Result(true, null);
  }

  private void execute(Executor executor, ExecuteParam param) {
    LocalDateTime start = LocalDateTime.now();
    try {
      logger.info("开始执行任务:{}，执行方式:{}", param.getName(), param.getType());
      TaskContext ctx = new TaskContext(param);
      executor.execute(ctx);
      this.notify(param.getName(), param.getId(), new Result(true, null), start,
          LocalDateTime.now());
      logger.info("任务执行成功, 耗时:{}", Duration.ofMillis(
          (LocalDateTime.now().toLocalTime().getSecond() - start.toLocalTime().getSecond())
              * 1000));
    } catch (Exception e) {
      String error = e.getMessage();
      if (StrKit.isBlank(error)) {
        error = e.toString();
      }
      this.notify(param.getName(), param.getId(), new Result(false, error), start,
          LocalDateTime.now());
      logger.error("任务执行失败, 耗时:{}, 错误信息:{}", Duration.ofMillis(
          (LocalDateTime.now().toLocalTime().getSecond() - start.toLocalTime().getSecond()) * 1000),
          Exceptions.getStackTrace(e));
    } finally {
      if (param.getType() == ExecuteParam.ExecuteType.AUTO) {
        runningTasks.remove(param.getName());
      }
    }
  }

  /**
   * 通知 skynet 任务执行结果
   *
   * @param name 任务名称
   * @param id 任务ID
   * @param result 执行结果
   * @param start 任务执行开始时间
   * @param end 任务执行结束时间
   */
  private void notify(String name, String id, Result result, LocalDateTime start,
      LocalDateTime end) {
    try {
      NotifyRequest param = new NotifyRequest();
      param.setId(id);
      param.setName(name);
      param.setResult(result);
      param.setStartTime(start);
      param.setEndTime(end);
      param.setResult(result);
      param.setIp(IPUtil.getLocalIP());
      Result nr = notifyService.notify(param);
      if (!nr.Success) {
        logger.error("通知任务状态错误, ID:{}, Name:{}, Error:{}", id, name, nr.ErrorInfo);
      }
    } catch (Exception e) {
      logger.error("通知任务状态异常, ID:{}, Name:{}, Error:", id, name, e);
    }
  }
}
