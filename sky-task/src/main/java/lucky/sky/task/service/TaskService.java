package lucky.sky.task.service;

import lucky.sky.net.rpc.annotation.RpcMethod;
import lucky.sky.net.rpc.annotation.RpcParameter;
import lucky.sky.net.rpc.annotation.RpcService;
import lucky.sky.task.data.ExecuteParam;
import lucky.sky.task.data.Result;

/**
 *
 */
@RpcService(description = "计划任务执行服务")
public interface TaskService {

  @RpcMethod(name = "Execute", description = "执行任务")
  @RpcParameter(description = "任务执行结果")
  Result execute(@RpcParameter(description = "任务参数") ExecuteParam param);
}
