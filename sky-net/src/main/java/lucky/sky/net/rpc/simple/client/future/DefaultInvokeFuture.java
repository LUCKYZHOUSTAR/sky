

package lucky.sky.net.rpc.simple.client.future;

import io.netty.util.Signal;
import lucky.sky.net.rpc.RpcError;
import lucky.sky.net.rpc.simple.data.SimpleResponseMessage;
import lucky.sky.util.lang.FaultException;
import lucky.sky.util.lang.StackTraceUtil;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class DefaultInvokeFuture<V> extends AbstractFuture<V> implements InvokeFuture<V> {

  private static final Logger logger = LoggerManager.getLogger(DefaultInvokeFuture.class);

  private static final long DEFAULT_TIMEOUT_NANOSECONDS = TimeUnit.MILLISECONDS.toNanos(3 * 1000);

  private static final ConcurrentMap<String, DefaultInvokeFuture<?>> roundFutures = new ConcurrentHashMap<>();

  private final String invokeId;
  private final long timeout;
  private final long startTime = System.nanoTime();

  private volatile boolean sent = false;

  public static <T> DefaultInvokeFuture<T> with(
      String invokeId, long timeoutMillis) {

    return new DefaultInvokeFuture<>(invokeId, timeoutMillis);
  }

  private DefaultInvokeFuture(
      String invokeId, long timeoutMillis) {
    this.invokeId = invokeId;
    this.timeout = timeoutMillis > 0 ? TimeUnit.MILLISECONDS.toNanos(timeoutMillis)
        : DEFAULT_TIMEOUT_NANOSECONDS;
    roundFutures.put(invokeId, this);

  }


  @Override
  public V getResult() throws Throwable {
    String error = null;
    try {
      return get(timeout, TimeUnit.NANOSECONDS);
    } catch (Signal s) {
      if (s == TIMEOUT) {
        error = String.format("获取服务器响应超时(%dms)", this.timeout);
        throw RpcError.CLIENT_READ_FAILED.toFault(error);
      } else {
        throw RpcError.CLIENT_UNKNOWN_ERROR.toFault();
      }
    }
  }


  public void markSent() {
    sent = true;
  }

  @SuppressWarnings("all")
  private void doReceived(SimpleResponseMessage response) {
    if (response.isSuccess()) {
      set((V) response);
    } else {
      setException(response);
    }
  }

  public static void received(SimpleResponseMessage response) {

    //把放置的缓存结果信息给移除
        /*
        1.如果移除的时候没有，代表响应结果超时，后台线程已经给移除掉了，无需再次doreive
        2.如果移除的时候有，代表还没有超时，放置结果即可
         */
    DefaultInvokeFuture<?> future = roundFutures.remove(response.getMessageID());
    if (future == null) {
      logger.warn("A timeout response [{}] finally returned on channel.", response);
      return;
    }

    future.doReceived(response);
  }

  private void setException(SimpleResponseMessage response) {
    Throwable cause;
    cause = new FaultException(response.getErrorCode(), response.getErrorDetail());
    setException(cause);
  }


  public static void fakeReceived(SimpleResponseMessage response) {
    DefaultInvokeFuture<?> future = null;
    future = roundFutures.remove(response.getMessageID());
    if (future == null) {
      return; // 正确结果在超时被处理之前返回
    }
    future.doReceived(response);
  }

  @Override
  protected void done(int state, Object x) {
    //do nothing
  }

  // timeout scanner用来处理，客户端发送失败，或者，服务器端处理失败的场景
  @SuppressWarnings("all")
  private static class TimeoutScanner implements Runnable {

    @Override
    public void run() {
      for (; ; ) {
        try {
          // round
          for (DefaultInvokeFuture<?> future : roundFutures.values()) {
            process(future);
          }
        } catch (Throwable t) {
          logger.error("An exception was caught while scanning the timeout futures {}.",
              StackTraceUtil.stackTrace(t));
        }

        try {
          Thread.sleep(30);
        } catch (InterruptedException ignored) {
        }
      }
    }

    private void process(DefaultInvokeFuture<?> future) {
      if (future == null || future.isDone()) {
        return;
      }

      if (System.nanoTime() - future.startTime > future.timeout) {

        SimpleResponseMessage response = new SimpleResponseMessage(future.invokeId);
        //代表响应失败
        response.setSuccess(false);
        response.setErrorCode(
            future.sent ? RpcError.SERVER_TIMEOUT.value() : RpcError.CLIENT_ERROR.value());
        DefaultInvokeFuture.fakeReceived(response);
      }
    }
  }

  static {
    Thread t = new Thread(new TimeoutScanner(), "timeout.scanner");
    t.setDaemon(true);
    t.start();
  }
}
