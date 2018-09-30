package lucky.sky.util.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供枚举实用功能。
 */
public class Enums {

  private final static Map<Class<?>, Map<Integer, Object>> caches = new ConcurrentHashMap<>();

  /**
   * 根据指定的枚举值获取枚举实例
   *
   * @throws RuntimeException 当枚举值未定义。
   */
  public static <T extends EnumValueSupport> T valueOf(Class<T> enumClass, int value) {
    return valueOf(enumClass, value, true);
  }

  /**
   * @param throwIfUndefined 指示枚举值未定义时是否抛出异常。
   */
  @SuppressWarnings("unchecked")
  public static <T extends EnumValueSupport> T valueOf(Class<T> enumClass, int value,
      boolean throwIfUndefined) {
    Map<Integer, Object> map = getValues(enumClass);
    Object v = map.get(value);
    if (v == null) {
      if (throwIfUndefined) {
        throw Exceptions.undefinedEnum(enumClass, value);
      }
      return null;
    }
    return (T) v;
  }

  /**
   * 判断是否为已定义的枚举值。
   */
  public static <T extends EnumValueSupport> boolean isDefined(Class<T> enumClass, int value) {
    Map<Integer, Object> map = getValues(enumClass);
    return map.containsKey(value);
  }

  /**
   * 获取指定枚举值的枚举显示名称。如果枚举值未定义，则返回空字符串。
   */
  public static <T extends EnumDisplayNameSupport & EnumValueSupport> String displayName(
      Class<T> enumClass, int value) {
    T e = valueOf(enumClass, value, false);
    if (e != null) {
      return e.displayName();
    }
    return "";
  }

  private static <T extends EnumValueSupport> Map<Integer, Object> getValues(Class<T> enumClass) {
    if (!enumClass.isEnum()) {
      throw new IllegalArgumentException(enumClass + " is not enum type");
    }
    Map<Integer, Object> map = caches.get(enumClass);
    if (map == null) {
      map = new HashMap<>();
      for (T v : enumClass.getEnumConstants()) {
        map.put(v.value(), v);
      }
      caches.put(enumClass, map);
    }
    return map;
  }
}

