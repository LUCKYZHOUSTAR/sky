package lucky.sky.util.context;

/**
 * 提供独立与特定类型应用（如Request/Reply, Desktop)的调用上下文容器。
 */
public interface InvokeContext {

  <T> T get(String name);

  <T> void set(String name, T value);
}
