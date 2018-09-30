package lucky.sky.mq.msg;

import java.util.HashMap;
import java.util.Map;
import lucky.sky.mq.msg.rocketmq.consumer.MetaQEventClient;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import lucky.sky.util.net.IPUtil;
import org.apache.rocketmq.client.exception.MQClientException;

/**
 * @Author:chaoqiang.zhou handler的执行器容器
 * @Date:Create in 下午6:30 2018/4/4
 */
public class MsgContainer {

  private static final Logger logger = LoggerManager.getLogger(MsgContainer.class);
  private HashMap<String, Handler> executors = new HashMap<>();

  private Map<String/**topic*/, String/**tags*/> subscribeMap = new HashMap<String, String>();

  private MetaQEventClient consumerClient;
  private  String consumeGroup = "C_Consumer";

  public MsgContainer(String consumeGroup) {
    consumerClient = new MetaQEventClient();
    consumerClient.setConsumerGroup(consumeGroup);
    consumerClient.setInstanceName(IPUtil.getLocalIP() + consumeGroup);
    //注册监听器
    //注册监听器
    consumerClient.setMessageListener(new SendMqmsgListener(this));
  }


  public void init() {

    try {
      //注册订阅的topic信息
      this.consumerClient.setSubscribeMap(subscribeMap);
      this.consumerClient.init();
    } catch (MQClientException ex) {
      logger.error("start mq consumer failed ,the error {}", ex);
    }
  }

  private String buildKey(String topic, String tags) {
    return topic + "." + tags;
  }

  public void registerHandler(Handler handler, String topic, String tag) {
    executors.put(buildKey(topic, tag), handler);
  }

  public Handler getHandler(String topic,String tag){
    return executors.get(buildKey(topic,tag));
  }

  public HashMap<String, Handler> getExecutors() {
    return executors;
  }

  public Map<String, String> getSubscribeMap() {
    return subscribeMap;
  }

  public void setSubscribeMap(Map<String, String> subscribeMap) {
    this.subscribeMap = subscribeMap;
  }

}
