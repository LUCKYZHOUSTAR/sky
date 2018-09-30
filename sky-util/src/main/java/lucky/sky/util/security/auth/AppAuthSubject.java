package lucky.sky.util.security.auth;

import lombok.Getter;

/**
 * 带有特定应用程序的认证主体。
 */
@Getter
public class AppAuthSubject extends GenericAuthSubject {

  int appId;

  public AppAuthSubject(int appId, String userName, String password) {
    super(userName, password);
    this.appId = appId;
  }

  public AppAuthSubject(int appId, String userName, String password,
      PasswordFormat passwordFormat) {
    super(userName, password, passwordFormat);
    this.appId = appId;
  }
}
