package lucky.sky.util.lang;

import org.apache.commons.lang3.text.StrBuilder;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 支持错误代码的异常类。
 */
public class FaultException extends RuntimeException {

  private final int errorCode;
  private Map<String, Object> data;

  public FaultException() {
    errorCode = -1;
  }

  public FaultException(String message) {
    super(message);
    errorCode = -1;
  }

  /**
   * 使用 MessageFormat.format 格式化，占位符格式为 {0} {1} {2}
   */
  public FaultException(String messageFormat, Object... messageArgs) {
    super(MessageFormat.format(messageFormat, messageArgs));
    errorCode = -1;
  }

  public FaultException(int errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public FaultException(int errorCode, String messageFormat, Object... messageArgs) {
    this(errorCode, MessageFormat.format(messageFormat, messageArgs));
  }

  public FaultException(int errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public FaultException(Throwable cause) {
    super(cause);
    errorCode = -1;
  }

  /**
   * 错误代码
   */
  public int getErrorCode() {
    return errorCode;
  }

  /**
   * 是否设置了错误（非零，非 -1）。
   */
  public boolean hasErrorCode() {
    return errorCode != 0 && errorCode != -1;
  }

  @Override
  public String toString() {
    String s = super.toString();
    if (hasErrorCode()) {
      s += " (errorCode: " + getErrorCode() + ")";
    }
    return s;
  }

  /**
   * 返回包含错误信息、堆栈详情、自定义数据的异常详情。
   */
  public String toStringDetail() {
    StrBuilder sb = new StrBuilder();
    sb
        .appendln(getStackTraceString())
        .append(getDataString())
        .appendNewLine();
    return sb.toString();
  }

  @Override
  public void printStackTrace(PrintStream s) {
    super.printStackTrace(s);
    if (data != null && data.size() > 0) {
      s.println();
      data.forEach((k, v) -> {
        s.append("\t").append(k).append(": ");
        s.print(v);
        s.println();
      });
    }
  }

  @Override
  public void printStackTrace(PrintWriter s) {
    super.printStackTrace(s);
    if (data != null && data.size() > 0) {
      s.println();
      data.forEach((k, v) -> {
        s.append("\t").append(k).append(": ");
        s.print(v);
        s.println();
      });
    }
  }

  /**
   * 返回堆栈详情
   */
  public String getStackTraceString() {
    return Exceptions.getStackTrace(this);
  }

  /**
   * 获取自定义数据。
   */
  public Map<String, Object> getData() {
    if (data == null) {
      data = new HashMap<>();
    }
    return data;
  }

  /**
   * 返回自定义数据。
   */
  public String getDataString() {
    if (data != null && data.size() > 0) {
      StrBuilder sb = new StrBuilder();
      data.forEach((k, v) -> sb.append(k).append(": ").append(v).appendNewLine());
      return sb.toString();
    }
    return StrKit.empty;
  }


  public static <T extends ErrorInfo> FaultException of(T error) {
    return new FaultException(error.getCode(), error.getMessage());
  }

  public static <T extends ErrorInfo> FaultException of(T error, Object... messageArgs) {
    return new FaultException(error.getCode(),
        MessageFormat.format(error.getMessage(), messageArgs));
  }

  public static <T extends ErrorInfo> FaultException of(Throwable cause, T error) {
    return new FaultException(error.getCode(), error.getMessage(), cause);
  }

  public static <T extends ErrorInfo> FaultException of(Throwable cause, T error,
      Object... messageArgs) {
    return new FaultException(error.getCode(),
        MessageFormat.format(error.getMessage(), messageArgs), cause);
  }
}
