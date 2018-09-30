package lucky.sky.db.jsd.converter;

import java.sql.Timestamp;
import java.util.Date;

/**
 * on 15/11/20.
 */
public class DateConverter implements Converter<Date> {

  @Override
  public Object j2d(Date value) {
    return new Timestamp(value.getTime());
  }

  @Override
  public Date d2j(Class<Date> type, Object value) {
    return (Date) value;
  }
}
