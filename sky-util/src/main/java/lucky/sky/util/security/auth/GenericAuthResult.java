package lucky.sky.util.security.auth;

import lombok.Getter;

/**
 * 通用身份认证结果。
 */
@Getter
public class GenericAuthResult implements AuthResult {

  int userId;

  public GenericAuthResult(int userId) {
    this.userId = userId;
  }
}
