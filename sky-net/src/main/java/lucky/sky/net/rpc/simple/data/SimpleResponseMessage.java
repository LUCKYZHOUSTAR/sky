package lucky.sky.net.rpc.simple.data;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lucky.sky.net.rpc.RpcError;
import lucky.sky.net.rpc.RpcException;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.lang.Exceptions;
import lucky.sky.util.lang.FaultException;
import lucky.sky.util.lang.StrKit;


@Getter
@Setter
public class SimpleResponseMessage{

  /**
   * 上下文 ID, 在整个请求链中保持不变
   */
  private String contextID;

  /**
   * 消息 ID, 每个请求唯一
   */
  private String messageID;

  /**
   * 是否成功
   */
  private boolean success;

  /**
   * 调用结果
   */
  private Object result;

  /**
   * 错误信息
   */
  private String errorInfo;

  /**
   * 服务器时间
   */
  private long serverTime;


  /**
   * 错误代码
   */
  private int errorCode;

  /**
   * 错误详情，如异常堆栈，一般只应该在 DEBUG 模式传播给客户端，便于快速调试
   */
  private String errorDetail;


  public SimpleResponseMessage() {
  }

  public SimpleResponseMessage(String messageID) {
    this.messageID = messageID;
  }

  public static SimpleResponseMessage success(Object result, String messageID) {
    SimpleResponseMessage message = new SimpleResponseMessage(messageID);
    message.setServerTime(System.currentTimeMillis());
    message.setSuccess(true);
    message.setResult(result);
    message.setErrorCode(RpcError.SERVER_OK.value());
    return message;
  }

  public static SimpleResponseMessage failed(Throwable e, String messageID) {
    SimpleResponseMessage message = new SimpleResponseMessage(messageID);
    message.setServerTime(System.currentTimeMillis());
    message.setSuccess(false);
    if (e instanceof RpcException) {
      message.setErrorCode(((RpcException) e).getErrorCode());
    } else if (e instanceof FaultException) {
      message.setErrorCode(((FaultException) e).getErrorCode());
    } else {
      message.setErrorCode(RpcError.SERVER_UNKNOWN_ERROR.value());
    }

    message.setErrorInfo(e.getMessage());
    if (StrKit.isBlank(message.getErrorInfo())) {
      message.setErrorInfo(e.toString());
    }
    message.setErrorDetail(Exceptions.getStackTrace(e));
    return message;
  }

  public static SimpleResponseMessage failed(RpcError ec, String messageID) {
    SimpleResponseMessage message = new SimpleResponseMessage(messageID);
    message.setSuccess(false);

    message.setServerTime(System.currentTimeMillis());
    message.setErrorCode(ec.value());
    message.setErrorInfo(ec.description());

    return message;
  }
}


