package lucky.sky.util.security.auth;

import lucky.sky.util.lang.EnumValueSupport;

/**
 * 定义身份认证错误编码常量。
 */
public enum AuthResultCode implements EnumValueSupport {
  /**
   * 账号或者密码错误
   */
  INVALID_CRENDENTIALS(1401, "账号或者密码错误"),
  /**
   * 用户已锁定
   */
  STATUS_LOCKOUT(1402, "用户已锁定");

  private int value;
  private String displayName;

  @Override
  public int value() {
    return this.value;
  }

  public String displayName() {
    return this.displayName;
  }

  AuthResultCode(int value, String displayName) {
    this.value = value;
    this.displayName = displayName;
  }
}
