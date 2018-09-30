package lucky.sky.db.jsd.converter;

/**
 * on 15/11/20.
 */
public interface Converter<T> {

  /**
   * Java 对象类型转换为数据库类型
   */
  Object j2d(T value);

  /**
   * 数据库类型转换成 Java 对象类型
   */
  T d2j(Class<T> type, Object value);
}
