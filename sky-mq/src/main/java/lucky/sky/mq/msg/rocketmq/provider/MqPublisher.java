package lucky.sky.mq.msg.rocketmq.provider;

import java.util.Map;
import lucky.sky.mq.msg.MsgDelayLevel;
import lucky.sky.mq.msg.MsgToMessage;
import lucky.sky.mq.msg.rocketmq.provider.result.MsgSendResult;
import lucky.sky.mq.msg.rocketmq.provider.result.MsgSendResultCode;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;


/**
 * metaq消息发布者
 */
public class MqPublisher implements Publisher {


  public static final MqPublisher INSTANCE = new MqPublisher();
  public static final String SEND_MESSAGE_WITH_VIP_CHANNEL_PROPERTY = "com.rocketmq.sendMessageWithVIPChannel";

  /**
   * 日志
   */
  private static final Logger logger = LoggerManager.getLogger(MqPublisher.class);

  /**
   * 生产者
   */
  private DefaultMQProducer producer;

  /**
   * MetaQ 相关参数配置,默认的发送者组的名称
   */
  private String groupId = "P_DefaultProducerName";


  public MqPublisher() {
    this.init();
  }

  /**
   * 初始化方法
   */
  public void init() {
    //防止rocketmq等vip通道，从而出现一些意外等惊喜

    System.setProperty(SEND_MESSAGE_WITH_VIP_CHANNEL_PROPERTY, "false");
    // 设置 nameserver 地址
    if (producer == null) {
      //10.1.54.121:9876;10.1.54.122:9876
      if (StringUtils.isBlank(AppConfig.getDefault().getGlobal().getNameSrvAdd())) {
        throw new IllegalArgumentException("the mq name srv address can not be blank or null");
      }
      producer = new DefaultMQProducer(groupId);
      producer.setNamesrvAddr(AppConfig.getDefault().getGlobal().getNameSrvAdd());

    }
    try {
      producer.start();
    } catch (MQClientException e) {
      logger.error("metaQ producer start failed!", e);
    }
  }


  /**
   * 结果日志
   */
  private String getDigestString(SendResult res, Message message) {
    StringBuffer sb = new StringBuffer();
    sb.append("MQ SendResult:");
    sb.append("(");
    sb.append(res.getSendStatus()).append(",");
    sb.append(res.getMsgId()).append(",");
    sb.append(message.getTopic()).append(",");
    sb.append(message.getTags()).append(",");
    if (res.getMessageQueue() != null) {
      sb.append(res.getMessageQueue().getBrokerName()).append(",");
      sb.append(res.getMessageQueue().getQueueId()).append(",");
    }
    sb.append(res.getQueueOffset());
    sb.append(")");

    return sb.toString();
  }

  private MsgSendResult sendMessage(Message message) {
    try {
      SendResult res = producer.send(message);

      if (res == null) {
        logger.error("result of send MQ message is null");
        return MsgSendResult.genFailedResult(MsgSendResultCode.SEND_FAILED.name());
      }
      if (logger.isDebugEnabled()) {
        logger.debug(getDigestString(res, message));
      }
      if (SendStatus.SEND_OK == res.getSendStatus()) {
        return MsgSendResult.genSuccessResult(res.getMsgId());
      }
    } catch (Exception e) {
      logger.error("meta send message failed. message={},error={}", message, e);
    }
    return MsgSendResult.genFailedResult(MsgSendResultCode.SEND_FAILED.name());
  }


  @Override
  public MsgSendResult sendMessage(String topic, String tag, Object payload) {
    return this.sendMessage(topic, tag, payload, null);
  }

  @Override
  public MsgSendResult sendDelayMessage(String topic, String tag, Object payload,
      MsgDelayLevel delayLevel) {
    Message msg = MsgToMessage.toMessage(topic, tag, payload, null, null);
    msg.setDelayTimeLevel(delayLevel.level());
    return null;
  }

  @Override
  public MsgSendResult sendMessage(String topic, String tag, Object payload, String messageKey) {
    return this.sendMessage(topic, tag, payload, null, messageKey);
  }


  /**
   * @param topic 要发送的topic
   * @param tag 要发送的tag
   * @param payload 要实际发送的消息体
   * @param properties 封装在message中的properties属性，需要业务自己封装好需要携带的参数
   */

  @Override
  public MsgSendResult sendMessage(String topic, String tag, Object payload,
      Map<String, String> properties, String messagekey) {
    Message msg = MsgToMessage.toMessage(topic, tag, payload, properties, messagekey);
    MsgSendResult res = this.sendMessage(msg);
    return res;
  }


  /**
   * 异步的发送mq消息，除非在一些特别敏感的场合才会使用
   */
  @Override
  public void send(String topic, String tag, Object payload,
      Map<String, String> properties, SendCallback sendCallback) {
    Message msg = MsgToMessage.toMessage(topic, tag, payload, properties, "");
    try {
      producer.send(msg, sendCallback);
    } catch (Exception e) {
      logger.error("meta send message failed. message={},error={}", msg, e);
    }
  }


  /**
   * destroy
   */
  public void destroy() {
    if (producer != null) {
      producer.shutdown();
    }
  }

  /**
   * setProducer
   */
  public void setProducer(DefaultMQProducer producer) {
    this.producer = producer;
  }

  /**
   * setGroupId
   */
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getGroupId() {
    return groupId;
  }
}
