package lucky.sky.util.lang;

import java.lang.reflect.ParameterizedType;

/**
 * 泛型类型包装器
 */
public abstract class TypeWrapper<T> {

  private final java.lang.reflect.Type type;

  protected TypeWrapper() {
    java.lang.reflect.Type superClass = this.getClass().getGenericSuperclass();
    this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
  }

  /**
   * 获取泛型参数的实际类型
   */
  public java.lang.reflect.Type getType() {
    return this.type;
  }
}
