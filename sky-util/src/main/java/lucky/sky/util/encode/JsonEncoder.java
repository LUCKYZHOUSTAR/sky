package lucky.sky.util.encode;

import com.alibaba.fastjson.JSON;
import lucky.sky.util.lang.TypeWrapper;


public class JsonEncoder implements StringEncoder {

  public final static JsonEncoder DEFAULT = new JsonEncoder();

  @Override
  public String encode(Object obj) {
    return JSON.toJSONString(obj);
  }

  @Override
  public <T> T decode(String value, Class<T> clazz) {
    return JSON.parseObject(value, clazz);
  }

  @Override
  public <T> T decode(String value, TypeWrapper<T> type) {
    return JSON.parseObject(value, type.getType());
  }

}
