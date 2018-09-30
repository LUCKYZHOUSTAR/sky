package lucky.sky.util.security.auth;

/**
 * 身份验证与授权提供器。
 */
public interface AuthProvider {

  AuthResult authenticate(AuthSubject subject);
}
