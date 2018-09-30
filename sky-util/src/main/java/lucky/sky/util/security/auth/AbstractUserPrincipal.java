package lucky.sky.util.security.auth;

import java.util.Objects;

/**
 * 抽象的用户安全主题。
 */
public abstract class AbstractUserPrincipal implements UserPrincipal {

  protected int userId;
  protected String userName;
  protected String[] authorities;

  public AbstractUserPrincipal(int userId, String userName) {
    this(userId, userName, new String[0]);
  }

  public AbstractUserPrincipal(int userId, String userName, String[] authorities) {
    Objects.requireNonNull(userName, "arg userName");
    Objects.requireNonNull(authorities, "arg authorities");
    //
    this.userId = userId;
    this.userName = userName;
    this.authorities = authorities;
  }

  /**
   * 获取关联的用户ID
   */
  @Override
  public int getUserId() {
    return userId;
  }

  /**
   * 获取关联的授权列表
   */
  @Override
  public String[] getAuthorities() {
    return authorities;
  }

  /**
   * Returns the name of this principal.
   *
   * @return the name of this principal.
   */
  @Override
  public String getName() {
    return userName;
  }
}
