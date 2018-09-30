package lucky.sky.net.rpc.simple.client;

import io.netty.util.internal.SystemPropertyUtil;
import lucky.sky.net.rpc.simple.client.task.MessageTask;
import lucky.sky.net.rpc.simple.data.JResponsePayload;
import lucky.sky.util.concurrent.NamedThreadFactory;

import java.util.concurrent.*;

/**
 * @Author:chaoqiang.zhou
 * @Description:consume thread group
 * @Date:Create in 16:35 2018/3/16
 */
public class DefaultConsumerProcessor {

  private static final RejectedExecutionHandler defaultHandler =
      new ThreadPoolExecutor.AbortPolicy();
  private static final Executor executor = new ThreadPoolExecutor(
      Runtime.getRuntime().availableProcessors() << 1,
      32,
      120L,
      TimeUnit.SECONDS,
      workQueue(),
      new NamedThreadFactory("sky-consumer-processor"), defaultHandler);

  public DefaultConsumerProcessor() {

  }

  public static void handleResponse(JResponsePayload responsePayload) throws Exception {
    MessageTask task = new MessageTask(responsePayload);
    if (executor == null) {
      task.run();
    } else {
      executor.execute(task);
    }
  }


  protected static int coreWorkers() {
    return SystemPropertyUtil.getInt("sky.executor.factory.consumer.core.workers",
        Runtime.getRuntime().availableProcessors() << 1);
  }

  protected static int maxWorkers() {
    return SystemPropertyUtil.getInt("sky.executor.factory.consumer.max.workers", 32);
  }

  private static BlockingQueue<Runnable> workQueue() {
    return new ArrayBlockingQueue<>(
        SystemPropertyUtil.getInt("sky.executor.factory.consumer.queue.capacity", 32768));
  }
}
