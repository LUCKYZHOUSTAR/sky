package lucky.sky.util.lang;

/**
 * 表示格式有关的异常。
 */
public class FormatException extends IllegalArgumentException {

  public FormatException() {
    // default ctor
  }

  public FormatException(String s) {
    super(s);
  }

  public FormatException(String message, Throwable cause) {
    super(message, cause);
  }

  public FormatException(Throwable cause) {
    super(cause);
  }
}
