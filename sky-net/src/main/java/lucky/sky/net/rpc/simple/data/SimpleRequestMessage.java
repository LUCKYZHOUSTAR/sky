package lucky.sky.net.rpc.simple.data;

import java.io.Serializable;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class SimpleRequestMessage {


  private String clientName;

  private String userToken;

  private String serviceName;

  private String methodName;

  private List<Object> parameters = new ArrayList<>();


  /**
   * 上下文 ID, 在整个请求链中保持不变
   */
  private String contextID;

  /**
   * 消息 ID, 每个请求唯一
   */
  private String messageID;
}
