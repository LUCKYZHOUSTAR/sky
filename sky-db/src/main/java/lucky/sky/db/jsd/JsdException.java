package lucky.sky.db.jsd;

/**
 * on 15/5/21.
 */
public class JsdException extends RuntimeException {

  public JsdException() {
    super();
  }

  public JsdException(String message) {
    super(message);
  }

  public JsdException(Throwable cause) {
    super(cause);
  }

  public JsdException(String message, Throwable cause) {
    super(message, cause);
  }
}
