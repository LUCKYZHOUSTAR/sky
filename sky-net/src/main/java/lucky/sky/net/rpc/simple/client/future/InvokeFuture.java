

package lucky.sky.net.rpc.simple.client.future;

public interface InvokeFuture<V> {

  /**
   * Waits for this future to be completed and get the result.
   */
  V getResult() throws Throwable;
}
