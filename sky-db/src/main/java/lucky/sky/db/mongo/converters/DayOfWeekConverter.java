package lucky.sky.db.mongo.converters;

import com.mongodb.DBObject;
import org.apache.commons.lang3.ClassUtils;
import org.mongodb.morphia.converters.ConverterException;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.logging.Logger;
import org.mongodb.morphia.logging.MorphiaLoggerFactory;
import org.mongodb.morphia.mapping.MappedField;

import java.time.DayOfWeek;

/**
 * Converts from/to strong-type DayOfWeek/BsonInt32 based @link DayOfWeek.getValue()
 */
public final class DayOfWeekConverter extends TypeConverter implements SimpleValueConverter {

  private static final Logger log = MorphiaLoggerFactory.get(DayOfWeekConverter.class);

  @Override
  protected boolean isSupported(Class<?> c, MappedField optionalExtraInfo) {
    return (c.isEnum() && ClassUtils.isAssignable(c, DayOfWeek.class));
  }

  /**
   * decode the {@link DBObject} and provide the corresponding java (type-safe) object <br><b>NOTE:
   * optionalExtraInfo might be null</b>
   */
  @Override
  public Object decode(Class<?> targetClass, Object fromDBObject, MappedField optionalExtraInfo) {
    if (fromDBObject == null) {
      return null;
    }

    if (fromDBObject instanceof DayOfWeek) {
      return fromDBObject;
    }

    if (fromDBObject instanceof Number) {
      return DayOfWeek.of(((Number) fromDBObject).intValue());
    }

    throw new ConverterException(String
        .format("Can't convert to DayOfWeek from %s@%s", fromDBObject.getClass(), fromDBObject));
  }

  @Override
  public Object encode(Object value, MappedField optionalExtraInfo) {
    if (value == null) {
      return null;
    }

    DayOfWeek day = (DayOfWeek) value;
    return day.getValue();
  }
}
