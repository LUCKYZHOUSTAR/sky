package lucky.sky.mq.msg.rocketmq.provider;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author:chaoqiang.zhou
 * @Date:Create in 上午10:18 2018/4/9 系统对外的mq的消息体
 */

@Getter
@Setter
public class Msg {


  private String topic;
  private String tag;
  //增加消息id，全局唯一，方便进行后台问题定位
  private String messageId;
  //可以指定messgekey，按照业务的维度，在后台可以统计
  private String messageKey;
  private Map<String, String> properties;
  private String body;
}
