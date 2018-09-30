package lucky.sky.util.security.auth;

import lombok.Getter;

/**
 * 通用安全主体。
 */
@Getter
public class GenericUserPrincipal extends AbstractUserPrincipal {

  public GenericUserPrincipal(int userId, String userName) {
    super(userId, userName);
  }

  public GenericUserPrincipal(int userId, String userName, String[] authorities) {
    super(userId, userName, authorities);
  }
}
