package lucky.sky.util.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class ConsoleLogger {

  private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("config");

  static {
    ConsoleHandler handler = new ConsoleHandler();
    handler.setFormatter(new Formatter() {
      @Override
      public String format(LogRecord record) {
        return String.format("[%s] %s%n", record.getLevel(), record.getMessage());
      }
    });
    logger.setLevel(Level.FINE);
    logger.setUseParentHandlers(false);
    logger.addHandler(handler);
  }

  private ConsoleLogger() {
    // 防止实例化
  }

  /**
   * 记录警告日志
   */
  public static void warn(String msg) {
    logger.warning(msg);
  }

  /**
   * 记录警告日志
   */
  public static void warn(String format, Object... args) {
    warn(String.format(format, args));
  }

  /**
   * 记录信息日志
   */
  public static void info(String msg) {
    logger.info(msg);
  }

  /**
   * 记录信息日志
   */
  public static void info(String format, Object... args) {
    info(String.format(format, args));
  }

  /**
   * 记录调试日志
   */
  public static void debug(String msg) {
    logger.fine(msg);
  }

  /**
   * 记录调试日志
   */
  public static void debug(String format, Object... args) {
    debug(String.format(format, args));
  }

  /**
   * 是否启用调试日志
   */
  public static boolean isDebugEnabled() {
    return logger.isLoggable(Level.FINE);
  }
}
