package lucky.sky.net.rpc.simple.server;

import io.netty.util.AttributeKey;

import java.util.Date;

/**
 * on 15/12/17.
 */
public class ChannelState {

  public static final AttributeKey<ChannelState> KEY = AttributeKey.newInstance("ChannelState");

  private final Date createTime = new Date();
  private Date activeTime;
  private String id = "";
  private String service;
  private String method;

  public ChannelState() {
    activeTime = createTime;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public Date getActiveTime() {
    return activeTime;
  }

  public String getId() {
    return id;
  }

  public String getService() {
    return service;
  }

  public String getMethod() {
    return method;
  }

  public void setActiveTime(Date activeTime) {
    this.activeTime = activeTime;
  }

  public void setId(String id) {
    if (id != null) {
      this.id = id;
    }
  }

  public void setService(String service) {
    this.service = service;
  }

  public void setMethod(String method) {
    this.method = method;
  }

}
