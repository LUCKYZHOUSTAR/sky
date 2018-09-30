package lucky.sky.db.mongo.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.LocalDateTime;

import static lucky.sky.util.convert.DateConverter.ofEpochMilli;
import static lucky.sky.util.convert.DateConverter.toEpochMilli;

/**
 * Encodes and decodes the Java 8 LocalDateTime object.
 */
public class LocalDateTimeCodec implements Codec<LocalDateTime> {

  @Override
  public LocalDateTime decode(BsonReader bsonReader, DecoderContext decoderContext) {
    return ofEpochMilli(bsonReader.readDateTime());
  }

  @Override
  public void encode(BsonWriter bsonWriter, LocalDateTime localDateTime,
      EncoderContext encoderContext) {
    bsonWriter.writeDateTime(toEpochMilli(localDateTime));
  }

  @Override
  public Class<LocalDateTime> getEncoderClass() {
    return LocalDateTime.class;
  }
}
