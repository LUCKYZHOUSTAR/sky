package lucky.sky.net.rpc.serialization.fastjson;


import com.alibaba.fastjson.JSON;
import lucky.sky.net.rpc.serialization.Serializer;

/**
 * @Author:chaoqiang.zhou
 * @Date:Create in 下午7:14 2018/5/9
 */
public class FastJsonSerializer extends Serializer {


  @Override
  public <T> byte[] writeObject(T obj) {
    return JSON.toJSONBytes(obj);
  }

  @Override
  public <T> T readObject(byte[] body, Class<T> clazz) {
    return JSON.parseObject(body, clazz);
  }


}
