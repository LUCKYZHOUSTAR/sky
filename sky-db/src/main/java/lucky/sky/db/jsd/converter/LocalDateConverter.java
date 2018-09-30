package lucky.sky.db.jsd.converter;

import java.sql.Date;
import java.time.LocalDate;

/**
 * on 16/3/2.
 */
public class LocalDateConverter implements Converter<LocalDate> {

  @Override
  public Object j2d(LocalDate value) {
    return Date.valueOf(value);
  }

  @Override
  public LocalDate d2j(Class<LocalDate> type, Object value) {
    return ((Date) value).toLocalDate();
  }
}
