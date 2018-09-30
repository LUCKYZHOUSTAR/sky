package lucky.sky.db.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Transformer;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.IdGenerator;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecRegistry;

import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;


public class ObjectCodec<T> implements CollectibleCodec<T> {

  private static final Logger log = LoggerManager.getLogger(ObjectCodec.class);

  private static final String ID_FIELD_NAME = "_id";
  private static BsonTypeClassMap bsonTypeClassMap = new BsonTypeClassMap();

  private final CodecRegistry registry;
  /**
   * mongodb的主键_id默认生成规则,可重新定义
   */
  private final IdGenerator idGenerator = new ObjectIdGenerator();
  private final Transformer valueTransformer = createTransformer();

  @SuppressWarnings("rawtypes")
  private final Class clazz;

  private final ObjectMethodCache.ObjectMethodUtil methodUtil;

  public ObjectCodec() {
    this(ObjectCodec.class, com.mongodb.MongoClient.getDefaultCodecRegistry());
  }

  @SuppressWarnings("rawtypes")
  public ObjectCodec(Class clazz, CodecRegistry registry) {
    this.clazz = clazz;
    this.methodUtil = ObjectMethodCache.getObjectMethodUtil(clazz);
    this.registry = registry;
  }

  /**
   * 后期可能需要扩展，具体根据实际情况
   */
  static Transformer createTransformer() {
    return new Transformer() {
      @Override
      public Object transform(final Object value) {
        return value;
      }
    };
  }


  /*
   * (non-Javadoc)
   *
   * @see org.bson.codecs.Encoder#encode(org.bson.BsonWriter,
   * java.lang.Object, org.bson.codecs.EncoderContext)
   */
  @Override
  public void encode(BsonWriter writer, T obj, EncoderContext encoderContext) {
    writer.writeStartDocument();
    Map<String, Object> keyValue = methodUtil.getObjectKeyValue(obj);
    for (Map.Entry<String, Object> entry : keyValue.entrySet()) {
      writer.writeName(entry.getKey());
      writeValue(writer, encoderContext, entry.getValue());
    }
    writer.writeEndDocument();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void writeValue(final BsonWriter writer, final EncoderContext encoderContext,
      final Object value) {
    if (value == null) {
      writer.writeNull();
    } else if (List.class.isAssignableFrom(value.getClass())) {
      writeList(writer, (List<Object>) value, encoderContext.getChildContext());
    } else if (Map.class.isAssignableFrom(value.getClass())) {
      writeMap(writer, (Map<String, Object>) value, encoderContext.getChildContext());
    } else {
      Codec codec = registry.get(value.getClass());
      encoderContext.encodeWithChildContext(codec, writer, value);
    }
  }

  private void writeList(final BsonWriter writer, final List<Object> list,
      final EncoderContext encoderContext) {
    writer.writeStartArray();
    for (final Object value : list) {
      writeValue(writer, encoderContext, value);
    }
    writer.writeEndArray();
  }

  private void writeMap(final BsonWriter writer, final Map<String, Object> map,
      final EncoderContext encoderContext) {
    writer.writeStartDocument();
    for (final Map.Entry<String, Object> entry : map.entrySet()) {
      writer.writeName(entry.getKey());
      writeValue(writer, encoderContext, entry.getValue());
    }
    writer.writeEndDocument();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.bson.codecs.Encoder#getEncoderClass()
   */
  @SuppressWarnings("unchecked")
  @Override
  public Class<T> getEncoderClass() {
    return clazz;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.bson.codecs.Decoder#decode(org.bson.BsonReader,
   * org.bson.codecs.DecoderContext)
   */
  @SuppressWarnings("unchecked")
  @Override
  public T decode(BsonReader reader, DecoderContext decoderContext) {
    Object obj = null;
    try {
      obj = clazz.newInstance();
    } catch (Exception e) {
      log.error("java invoke error clazz.newInstance ", e);
    }

    if (null == obj) {
      return null;
    }
    reader.readStartDocument();
    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      String fieldName = reader.readName();
      Class<?> clazz = methodUtil.getClass(fieldName);
      Object value = readValue(reader, decoderContext, clazz);
      methodUtil.setBeanValue(obj, fieldName, value);
    }
    reader.readEndDocument();
    return (T) obj;
  }


  private Object readValue(final BsonReader reader, final DecoderContext decoderContext,
      Class<?> clazz) {
    BsonType bsonType = reader.getCurrentBsonType();
    if (bsonType == BsonType.NULL) {
      reader.readNull();
      return null;
    }
    if (bsonType == BsonType.ARRAY) {
      return readList(reader, decoderContext, clazz);
    }
//		boolean b = (bsonType == BsonType.BOOLEAN
//				|| bsonType == BsonType.STRING
//				|| bsonType == BsonType.DATE_TIME
//				|| bsonType == BsonType.DOUBLE || bsonType == BsonType.INT32 || bsonType == BsonType.INT64);
//			if (null != clazz && !b) {
    if (null != clazz) {
      return registry.get(clazz).decode(reader, decoderContext);
    }
    return valueTransformer.transform(registry.get(bsonTypeClassMap.get(bsonType)).decode(
        reader, decoderContext));
  }

  private List<Object> readList(final BsonReader reader, final DecoderContext decoderContext,
      Class<?> clazz) {
    reader.readStartArray();
    List<Object> list = new ArrayList<Object>();
    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      list.add(readValue(reader, decoderContext, clazz));
    }
    reader.readEndArray();
    return list;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.bson.codecs.CollectibleCodec#generateIdIfAbsentFromDocument(java.
   * lang.Object)
   */
  @Override
  public T generateIdIfAbsentFromDocument(T obj) {
    if (!documentHasId(obj)) {
      methodUtil.setBeanValue(obj, ID_FIELD_NAME, idGenerator.generate());
    }
    return obj;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.bson.codecs.CollectibleCodec#documentHasId(java.lang.Object)
   */
  @Override
  public boolean documentHasId(T obj) {
    return (null != getObjectId(obj));
  }

  /**
   * 获取实体对应mongodb主键_id的值
   */
  private Object getObjectId(T obj) {
    return methodUtil.getBeanValue(obj, ID_FIELD_NAME);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.bson.codecs.CollectibleCodec#getDocumentId(java.lang.Object)
   */
  @Override
  public BsonValue getDocumentId(T obj) {
    if (!documentHasId(obj)) {
      throw new IllegalStateException("The Object does not contain an _id");
    }

    Object id = getObjectId(obj);
    if (id instanceof BsonValue) {
      return (BsonValue) id;
    }
    BsonDocument idHoldingDocument = new BsonDocument();
    BsonWriter writer = new BsonDocumentWriter(idHoldingDocument);
    writer.writeStartDocument();
    writer.writeName(ID_FIELD_NAME);
    writeValue(writer, EncoderContext.builder().build(), id);
    writer.writeEndDocument();
    return idHoldingDocument.get(ID_FIELD_NAME);
  }

}
