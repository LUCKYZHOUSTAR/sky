package lucky.sky.db.jsd.converter;

import lucky.sky.util.lang.EnumValueSupport;
import lucky.sky.util.lang.Enums;

/**
 * on 15/11/20.
 */
public class EnumConverter<T extends Enum & EnumValueSupport> implements Converter<T> {

  @Override
  public Object j2d(T value) {
    return value.value();
  }

  @Override
  public T d2j(Class<T> type, Object value) {
    return Enums.valueOf(type, (int) value);
  }
}
