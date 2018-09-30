package lucky.sky.web;

import lucky.sky.util.lang.FaultException;

/**
 * 表示请求验证失败异常。
 */
public class HttpRequestValidationException extends FaultException {
    public HttpRequestValidationException() {
        this("请求参数验证失败，请确保没有提交 HTML 代码");
    }

    public HttpRequestValidationException(String message) {
        super(message);
    }

    /**
     * 使用 MessageFormat.format 格式化，占位符格式为 {0} {1} {2}
     *
     * @param messageFormat
     * @param messageArgs
     */
    public HttpRequestValidationException(String messageFormat, Object... messageArgs) {
        super(messageFormat, messageArgs);
    }

    public HttpRequestValidationException(int errorCode, String message) {
        super(errorCode, message);
    }

    public HttpRequestValidationException(int errorCode, String messageFormat, Object... messageArgs) {
        super(errorCode, messageFormat, messageArgs);
    }

    public HttpRequestValidationException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public HttpRequestValidationException(Throwable cause) {
        super(cause);
    }
}
