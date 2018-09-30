package lucky.sky.util.encode;

import lucky.sky.util.lang.TypeWrapper;


public interface BinaryEncoder {

  /**
   * 序列化对象为字符串
   */
  byte[] encode(Object obj);

  /**
   * 从字符串反序列化对象
   *
   * @param value 序列化的字符串
   * @param clazz 对象类型
   * @param <T> 对象类型
   */
  <T> T decode(byte[] value, Class<T> clazz);

  /**
   * 从字符串反序列化对象
   *
   * @param value 从字符串反序列化对象
   * @param type 对象类型信息
   * @param <T> 对象类型
   */
  <T> T decode(byte[] value, TypeWrapper<T> type);
}
