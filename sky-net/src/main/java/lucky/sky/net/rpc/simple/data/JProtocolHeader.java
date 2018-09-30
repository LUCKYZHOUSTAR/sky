

package lucky.sky.net.rpc.simple.data;


/**
 * MAGIC:2个字节 Sign：消息标志位，来表示消息类型request/response/heartbeat等 bodysize：消息体body长度，int类型
 * bodycontent：消息体内容
 */
public class JProtocolHeader {

  /**
   * Magic
   */
  public static final short MAGIC = (short) 0xbabe;

  /**
   * Message Code: 0x01 ~ 0x0f ===================================================================================
   */
  public static final byte REQUEST = 0x01;     // Request
  public static final byte RESPONSE = 0x02;     // Response
  public static final byte ACK = 0x03;     // Acknowledge
  public static final byte HEARTBEAT = 0x04;     // Heartbeat

}
