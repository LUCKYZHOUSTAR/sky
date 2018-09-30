package lucky.sky.util.security.auth;

import lombok.Getter;

@Getter
public class GenericAuthSubject implements AuthSubject {

  String userName;
  String password;
  PasswordFormat passwordFormat = PasswordFormat.PLAIN;

  public GenericAuthSubject(String userName, String password) {
    this.userName = userName;
    this.password = password;
  }

  public GenericAuthSubject(String userName, String password, PasswordFormat passwordFormat) {
    this(userName, password);
    this.passwordFormat = passwordFormat;
  }
}
