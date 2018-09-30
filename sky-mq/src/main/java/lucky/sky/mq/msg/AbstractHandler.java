package lucky.sky.mq.msg;

import com.alibaba.fastjson.JSON;
import lucky.sky.mq.msg.rocketmq.provider.Msg;
import lucky.sky.util.lang.Exceptions;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.core.ResolvableType;

/**
 * @Author:chaoqiang.zhou
 * @Date:Create in 上午10:30 2018/4/9
 */
public abstract class AbstractHandler<T> implements Handler {

  protected Class<T> msgClass;

  public AbstractHandler() {
    //获取基类的对应类型的信息
    ResolvableType resolvableType = ResolvableType.forClass(this.getClass());
    msgClass = (Class<T>) (resolvableType.getSuperType().getGeneric(0).resolve());
  }


  @Override
  public void process(MessageExt m) {
    try {
      Msg msg = MsgToMessage.toMsg(m);
      T obj = decodeMessage(msg.getBody());
      this.process(obj, msg);
    } catch (Exception e) {
      throw Exceptions.asUnchecked(e);
    }
  }

  @SuppressWarnings("unchecked")
  private T decodeMessage(String body) {
    T msg;
    try {
      msg = JSON.parseObject(body, msgClass);
    } catch (Exception e) {
      if (msgClass == String.class) {
        msg = (T) body;
      } else {
        throw Exceptions.asUnchecked(e);
      }
    }
    return msg;
  }

  protected abstract void process(T raw, Msg msg);
}
