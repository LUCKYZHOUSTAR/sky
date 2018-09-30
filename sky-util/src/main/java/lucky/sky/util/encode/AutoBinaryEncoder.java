package lucky.sky.util.encode;

import lucky.sky.util.lang.Exceptions;
import lucky.sky.util.lang.TypeWrapper;


public class AutoBinaryEncoder implements BinaryEncoder {

  private static JsonBinaryEncoder jsonEncoder = JsonBinaryEncoder.DEFAULT;

  @Override
  public byte[] encode(Object obj) {
    if (obj instanceof BinaryEncodable) {
      return ((BinaryEncodable) obj).encode();
    } else {
      return jsonEncoder.encode(obj);
    }
  }

  @Override
  public <T> T decode(byte[] value, Class<T> clazz) {
    if (BinaryEncodable.class.isAssignableFrom(clazz)) {
      try {
        T obj = clazz.newInstance();
        ((BinaryEncodable) obj).decode(value);
        return obj;
      } catch (Exception e) {
        throw Exceptions.asUnchecked(e);
      }
    } else {
      return jsonEncoder.decode(value, clazz);
    }
  }

  @Override
  public <T> T decode(byte[] value, TypeWrapper<T> type) {
    return jsonEncoder.decode(value, type);
  }
}
