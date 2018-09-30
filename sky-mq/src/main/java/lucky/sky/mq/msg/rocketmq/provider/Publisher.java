
package lucky.sky.mq.msg.rocketmq.provider;

import java.io.IOException;
import java.util.Map;
import lucky.sky.mq.msg.MsgDelayLevel;
import lucky.sky.mq.msg.rocketmq.provider.result.MsgSendResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * metaq发布消息接口
 */
public interface Publisher {


  static Publisher get() {
    return MqPublisher.INSTANCE;
  }


  /**
   * 发送metaq消息
   *
   * @param topic 要发送的topic
   * @param tag 要发送的tag
   * @param payload 要实际发送的消息体
   */
  public MsgSendResult sendMessage(String topic, String tag, Object payload);


  /**
   * 发送metaq消息
   *
   * @param topic 要发送的topic
   * @param tag 要发送的tag
   * @param payload 要实际发送的消息体
   */
  public MsgSendResult sendDelayMessage(String topic, String tag, Object payload,
      MsgDelayLevel delayLevel);

  /**
   * @param topic 消息的topic
   * @param tag 消息对应的tag信息
   * @param payload 消息体
   * @param messageKey 业务对应的key，方便后续在后台进心查询有关该消息的信息
   */
  public MsgSendResult sendMessage(String topic, String tag, Object payload, String messageKey);

  /**
   * 发送metaq消息
   *
   * @param topic 要发送的topic
   * @param tag 要发送的tag
   * @param payload 要实际发送的消息体
   * @param properties 封装在message中的properties属性，需要业务自己封装好需要携带的参数
   */
  public MsgSendResult sendMessage(String topic, String tag, Object payload,
      Map<String, String> properties, String messageKey);


  /**
   * 异步的发送消息
   */
  public void send(String topic, String tag, Object payload,
      Map<String, String> properties, SendCallback sendCallback)
      throws MQClientException, RemotingException, InterruptedException;

}
