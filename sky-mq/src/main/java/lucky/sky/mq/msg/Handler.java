package lucky.sky.mq.msg;


import org.apache.rocketmq.common.message.MessageExt;

@FunctionalInterface
public interface Handler {

  void process(MessageExt message);
}
