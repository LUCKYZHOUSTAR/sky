package lucky.sky.db.mongo.converters;

import org.bson.BsonDateTime;
import org.mongodb.morphia.converters.ConverterException;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.logging.Logger;
import org.mongodb.morphia.logging.MorphiaLoggerFactory;
import org.mongodb.morphia.mapping.MappedField;

import java.time.LocalDateTime;
import java.util.Date;

import static lucky.sky.util.convert.DateConverter.*;


/**
 * Converts from/to Java 8 LocalDateTime/BsonDate. 基于系统时区转换。
 */
public final class LocalDateTimeConverter extends TypeConverter implements SimpleValueConverter {

  private static final Logger log = MorphiaLoggerFactory.get(LocalDateTimeConverter.class);

  public LocalDateTimeConverter() {
    super(LocalDateTime.class);
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

    if (fromDBObject instanceof LocalDateTime) {
      return fromDBObject;
    }

    if (fromDBObject instanceof Number) {
      return ofEpochMilli(((Number) fromDBObject).longValue());
    }

    if (fromDBObject instanceof Date) {
      return toLocalDateTime((Date) fromDBObject);
    }

    throw new ConverterException(String
        .format("Can't convert to LocalDateTime from %s@%s", fromDBObject.getClass(),
            fromDBObject));
  }

  @Override
  public Object encode(Object value, MappedField optionalExtraInfo) {
    if (value == null) {
      return null;
    }

    LocalDateTime dt = (LocalDateTime) value;
    return new BsonDateTime(toEpochMilli(dt));
  }
}
