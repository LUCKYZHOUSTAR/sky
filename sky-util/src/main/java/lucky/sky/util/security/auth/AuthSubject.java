package lucky.sky.util.security.auth;

/**
 * 身份认证主体。
 */
public interface AuthSubject {

  String getUserName();

  String getPassword();

  PasswordFormat getPasswordFormat();
}
