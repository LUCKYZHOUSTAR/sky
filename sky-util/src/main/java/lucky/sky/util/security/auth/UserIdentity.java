package lucky.sky.util.security.auth;

/**
 * 定义用户身份接口。
 */
public interface UserIdentity<K> {

  /**
   * 用户ID
   */
  K getId();

  /**
   * 用户名，一般可用于登录
   */
  String getUserName();

  /**
   * 此用户关联的邮箱
   */
  String getEmail();

  /**
   * 用户显示名称，例如完整姓名
   */
  String getDisplayName();
}
