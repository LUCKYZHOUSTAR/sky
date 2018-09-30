package lucky.sky.net.rpc.simple.data;

/**
 * @Author:chaoqiang.zhou
 * @Description:
 * @Date:Create in 17:34 2018/3/16
 */
public enum MsgType {

  REQUEST(1, "请求"),
  RESPONSE(2, "响应");

  MsgType(int value, String description) {
    this.value = value;
    this.description = description;
  }

  private int value;
  private String description;

  public int value() {
    return value;
  }
}
