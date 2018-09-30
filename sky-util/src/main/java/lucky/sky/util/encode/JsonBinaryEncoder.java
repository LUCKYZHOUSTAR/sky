package lucky.sky.util.encode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lucky.sky.util.lang.TypeWrapper;


public class JsonBinaryEncoder implements BinaryEncoder {

  public final static JsonBinaryEncoder DEFAULT = new JsonBinaryEncoder();
  private final static SerializerFeature[] features = new SerializerFeature[0];

  @Override
  public byte[] encode(Object obj) {
    return JSON.toJSONBytes(obj, features);
  }

  @Override
  public <T> T decode(byte[] value, Class<T> clazz) {
    return JSON.parseObject(value, clazz);
  }

  @Override
  public <T> T decode(byte[] value, TypeWrapper<T> type) {
    return JSON.parseObject(value, type.getType());
  }
}
