package lucky.sky.util.security.auth;

import lucky.sky.util.lang.Enums;
import lucky.sky.util.lang.EnumValueSupport;

/**
 * 密码格式。
 */
public enum PasswordFormat implements EnumValueSupport {
  /**
   * 明文，未加密
   */
  PLAIN(1, "明文，未加密"),
  /**
   * 使用 MD5 哈希算法加密
   */
  MD5(2, "使用 MD5 哈希算法加密");

  int value;
  String displayName;

  PasswordFormat(int value, String displayName) {
    this.value = value;
    this.displayName = displayName;
  }

  @Override
  public int value() {
    return this.value;
  }

  public String displayName() {
    return this.displayName;
  }

  public static PasswordFormat valueOf(int value) {
    return Enums.valueOf(PasswordFormat.class, value);
  }
}
