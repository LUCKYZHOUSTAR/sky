package lucky.sky.util.lang;

/**
 * 表示对象不存在的异常
 */
public class NotFoundObjectException extends FaultException {

  public NotFoundObjectException() {
    // default ctor
  }

  public NotFoundObjectException(String objectName, Object objectId) {
    this("Not found {0} with id {1}", objectName, objectId);
  }

  public NotFoundObjectException(String message) {
    super(message);
  }

  /**
   * 使用 MessageFormat.format 格式化，占位符格式为 {0} {1} {2}
   */
  public NotFoundObjectException(String messageFormat, Object... messageArgs) {
    super(messageFormat, messageArgs);
  }

  public NotFoundObjectException(int errorCode, String message) {
    super(errorCode, message);
  }

  public NotFoundObjectException(int errorCode, String messageFormat, Object... messageArgs) {
    super(errorCode, messageFormat, messageArgs);
  }

  public NotFoundObjectException(int errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }

  public NotFoundObjectException(Throwable cause) {
    super(cause);
  }
}
