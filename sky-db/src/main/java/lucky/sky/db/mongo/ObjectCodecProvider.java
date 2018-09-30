package lucky.sky.db.mongo;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * 类似过滤器，根据泛型类class来判断使用具体的映射规则进行映射封装
 *
 * 使用注解来处理
 */
public class ObjectCodecProvider implements CodecProvider {

  /* (non-Javadoc)
   * @see org.bson.codecs.configuration.CodecProvider#get(java.lang.Class, org.bson.codecs.configuration.CodecRegistry)
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public <T> org.bson.codecs.Codec<T> get(Class<T> clazz, CodecRegistry registry) {
    //根据注解判断
    if (clazz.isAnnotationPresent(Bson.class)) {
//			return (Codec<T>) new ObjectCodec(clazz);
      //综合考虑，为了兼容非泛型连接，需要在连接时候定义所有registry并在这里传递
      return (org.bson.codecs.Codec<T>) new ObjectCodec(clazz, registry);
    }
    return null;
  }

}
