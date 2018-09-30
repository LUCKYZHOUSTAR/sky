package lucky.sky.task.service;

import lucky.sky.net.rpc.annotation.RpcMethod;
import lucky.sky.net.rpc.annotation.RpcService;
import lucky.sky.task.data.*;

/**
 *
 */
@RpcService(description = "计划任务调度服务")
public interface ScheduleService {

  @RpcMethod(description = "通知任务执行结果")
  Result notify(NotifyRequest param);
}