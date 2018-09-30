package lucky.sky.util.context;

import lucky.sky.util.ioc.ServiceLocator;

/**
 * 提供当前 InvokeContext 实例访问功能。
 */
public class InvokeContextHolder {

  private static InvokeContext ctx;

  public static void set(InvokeContext invokeContext) {
    InvokeContextHolder.ctx = invokeContext;
  }

  public static InvokeContext get() {
    if (ctx == null) {
      ctx = ServiceLocator.current().getInstance(InvokeContext.class);
    }
    return ctx;
  }

  public static InvokeContext current() {
    InvokeContext ctx = get();
    if (ctx == null) {
      throw new IllegalStateException("no InvokeContext provided");
    }
    return ctx;
  }
}
