package lucky.sky.util.security.auth;

import lombok.Getter;
import lucky.sky.util.lang.FaultException;

/**
 * 表示拒绝访问异常。
 */
@Deprecated
public class AccessDeniedException extends FaultException {

  /**
   * 被拒绝的授权信息。
   */
  @Getter
  private final String authority;

  public AccessDeniedException() {
    this.authority = null;
  }

  public AccessDeniedException(String message) {
    super(message);
    this.authority = null;
  }

  public AccessDeniedException(String message, String authority) {
    super(message);
    this.authority = authority;
  }

  public AccessDeniedException(String message, String authority, Throwable cause) {
    super(message, cause);
    this.authority = authority;
  }

  public AccessDeniedException(String message, Throwable cause) {
    super(message, cause);
    this.authority = null;
  }
}
