package lucky.sky.util.lang;

/**
 * 定义错误信息接口。
 */
public interface ErrorInfo {

  /**
   * 错误代码。
   */
  int getCode();

  /**
   * 错误信息。
   */
  String getMessage();
}
