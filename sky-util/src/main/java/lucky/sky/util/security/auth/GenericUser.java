package lucky.sky.util.security.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * 表示通用用户信息。
 *
 */
@Getter
@Setter
public class GenericUser<K> implements UserIdentity {

  /**
   * 用户ID
   */
  private K id;
  /**
   * 用户名，一般可用于登录
   */
  private String userName;
  /**
   * 此用户关联的邮箱
   */
  private String email;
  /**
   * 用户显示名称，例如完整姓名
   */
  private String displayName;
}
