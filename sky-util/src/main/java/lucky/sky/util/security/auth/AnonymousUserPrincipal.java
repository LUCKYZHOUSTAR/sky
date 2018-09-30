package lucky.sky.util.security.auth;

/**
 * 表示匿名用户安全主体。
 */
public final class AnonymousUserPrincipal extends AbstractUserPrincipal {

  private static final UserPrincipal singleton = new AnonymousUserPrincipal();

  public static UserPrincipal getInstance() {
    return singleton;
  }

  private AnonymousUserPrincipal() {
    super(0, "", new String[0]);
  }

  @Override
  public boolean isAuthenticated() {
    return false;
  }
}
