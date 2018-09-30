package lucky.sky.mq.msg;

import com.alibaba.fastjson.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lucky.sky.mq.msg.rocketmq.provider.Msg;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.util.CollectionUtils;

/**
 * @Author:chaoqiang.zhou
 * @Date:Create in 上午10:42 2018/4/9
 */
public class MsgToMessage {


  public static Msg toMsg(MessageExt message) {
    Msg msg = new Msg();
    msg.setProperties(message.getProperties());
    msg.setTag(message.getTags());
    msg.setTopic(message.getTopic());
    msg.setMessageId(message.getMsgId());
    String body = new String(message.getBody(), StandardCharsets.UTF_8);
    msg.setBody(body);
    return msg;
  }

  public static Message toMessage(String topic, String tag, Object payload,
      Map<String, String> properties, String messageKey) {
    Message message = new Message();
    message.setTopic(topic);
    message.setTags(tag);
    if (StringUtils.isNotBlank(messageKey)) {
      message.setKeys(messageKey);
    }
    message.setKeys(messageKey);
    // 加入扩展信息
    if (!CollectionUtils.isEmpty(properties)) {
      Map<String, String> currentProperties = message.getProperties();
      currentProperties.putAll(properties);
    }
    String str = JSONObject.toJSONString(payload);
    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
    message.setBody(bytes);
    return message;
  }
}
