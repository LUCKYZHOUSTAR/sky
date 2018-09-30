package lucky.sky.mq.msg;

import java.util.List;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

public class SendMqmsgListener implements MessageListenerConcurrently {


  private MsgContainer msgContainer;


  public SendMqmsgListener(MsgContainer msgContainer) {
    this.msgContainer = msgContainer;
  }

  @Override
  public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
      ConsumeConcurrentlyContext consumeConcurrentlyContext) {
    Boolean result = true;
    try {
      list.forEach(msg -> {
        Handler handler = msgContainer.getHandler(msg.getTopic(), msg.getTags());
        handler.process(msg);
      });
    } catch (Exception e) {
      result = false;
    }

    //消息消费的时候，下游的消息要做幂等操作
    if (result) {
      return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    } else {
      return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
  }
}
