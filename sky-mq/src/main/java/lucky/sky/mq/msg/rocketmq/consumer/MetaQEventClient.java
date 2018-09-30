
package lucky.sky.mq.msg.rocketmq.consumer;

import java.util.HashMap;
import java.util.Map;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * MetaQ数据接收客户端，主要功能： -- 订阅metaQ数据
 */
public class MetaQEventClient implements MetaQConsumerClient {

  private static final Logger logger = LoggerManager.getLogger(MetaQEventClient.class);
  public static final String SEND_MESSAGE_WITH_VIP_CHANNEL_PROPERTY = "com.rocketmq.sendMessageWithVIPChannel";
  private String instanceName;
  private String consumerGroup;
  private String consumeThreadMax;
  private String consumeThreadMin;

  /**
   * metaQ 数据消费者
   */
  protected DefaultMQPushConsumer consumer;

  /**
   * MetaQ 相关参数配置
   */
  private Map<String/**topic*/, String/**tags*/> subscribeMap = new HashMap<String, String>();

  /**
   * metaq 并发线数
   */
  protected int consumerCorePoolSize = 1;


  /**
   * 网络的超时时间
   */
  private static final long consumeTimeOut = 2000L;


  /**
   * 处理进程
   */
  private MessageListenerConcurrently messageListener;

  /**
   * metaq consumer内部具有并发能力，并发数可配置，建议应用这边只起一个consumer，利于并发性能调整。
   */
  @Override
  public void init() throws MQClientException {
    //防止rocketmq等vip等channel，带来等意外的惊喜
    System.setProperty(SEND_MESSAGE_WITH_VIP_CHANNEL_PROPERTY, "false");
    logger.info("Starting MetaQ Consumer...");
    consumer = new DefaultMQPushConsumer();
    consumer.setInstanceName(instanceName);
    consumer.setConsumerGroup(consumerGroup);
    consumer.setNamesrvAddr(AppConfig.getDefault().getGlobal().getNameSrvAdd());
    //mq底层默认的64，足够使用
//    consumer.setConsumeThreadMin(Integer.parseInt(consumeThreadMin));
//    consumer.setConsumeThreadMax(Integer.parseInt(consumeThreadMax));
    consumer.setConsumeTimeout(consumeTimeOut);
    logger.info("consumer.consumerGroup:%s", consumer.getConsumerGroup());
    // 重上次消费进度消费
    consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

    //每次只push一个msg
    consumer.setConsumeMessageBatchMaxSize(1);
    //集群消费模式
    consumer.setMessageModel(MessageModel.CLUSTERING);
    // 添加metaQ订阅关系
    for (String topic : subscribeMap.keySet()) {
      consumer.subscribe(topic, subscribeMap.get(topic));
      logger.info("Consumer Subscription:%s", consumer.getSubscription());
    }

    // 挂载数据消费实例
    consumer.registerMessageListener(messageListener);
    logger.info("Ready to start consumer...[%s]", consumer);
    consumer.start();
    logger.info("MetaQ Consumer Started.");
  }

  @Override
  public void shown() {
    consumer.shutdown();
  }

  public void suspendConsumer() {
    consumer.suspend();
  }

  /**
   */
  public void resumeConsumer() {
    consumer.resume();
  }

  /**
   */
  @Override
  public void updateConsumerCorePoolSize(int corePoolSize) {
    consumer.updateCorePoolSize(corePoolSize);
  }

  /**
   */
  @Override
  public MessageExt viewMessage(String msgId) throws Exception {
    if (StringUtils.isEmpty(msgId)) {
      return null;
    }
    return consumer.viewMessage(msgId);
  }

  /**
   * setConsumerCorePoolSize
   *
   * @painaram consumerCorePoolSize
   */
  public void setConsumerCorePoolSize(int consumerCorePoolSize) {
    this.consumerCorePoolSize = consumerCorePoolSize;
  }


  public String getInstanceName() {
    return instanceName;
  }

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }

  public String getConsumerGroup() {
    return consumerGroup;
  }

  public void setConsumerGroup(String consumerGroup) {
    this.consumerGroup = consumerGroup;
  }


  public String getConsumeThreadMax() {
    return consumeThreadMax;
  }

  public void setConsumeThreadMax(String consumeThreadMax) {
    this.consumeThreadMax = consumeThreadMax;
  }

  public String getConsumeThreadMin() {
    return consumeThreadMin;
  }

  public void setConsumeThreadMin(String consumeThreadMin) {
    this.consumeThreadMin = consumeThreadMin;
  }

  public MessageListenerConcurrently getMessageListener() {
    return messageListener;
  }

  public void setMessageListener(MessageListenerConcurrently messageListener) {
    this.messageListener = messageListener;
  }

  public void setSubscribeMap(Map<String, String> subscribeMap) {
    this.subscribeMap = subscribeMap;
  }


}
