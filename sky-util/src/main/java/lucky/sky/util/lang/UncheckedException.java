package lucky.sky.util.lang;

/**
 * 把 checked 异常转换成 unchecked 异常的包装类
 *
 */
public class UncheckedException extends RuntimeException {

  public UncheckedException() {
    super();
  }

  public UncheckedException(String msg) {
    super(msg);
  }

  public UncheckedException(Throwable inner) {
    super(inner);
  }

  public UncheckedException(String msg, Throwable inner) {
    super(msg, inner);
  }
}
