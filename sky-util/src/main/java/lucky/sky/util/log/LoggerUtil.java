package lucky.sky.util.log;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * 日志打印工具类
 * yank
 */
public class LoggerUtil {
  
  /** debugLogger */
  public static void debug(Object msg) {
    Logger.getLogger("debugLogger").debug(msg);
  }
  


  /** 记录本系统调用外部系统接口的Logger */
  @Deprecated
  public static void inOutInfo(String msg) {
    Logger.getLogger("inOutLogger").info(msg);
  }

  /** 记录外部系统调用本系统接口的Logger */
  @Deprecated
  public static void outInInfo(String msg) {
    Logger.getLogger("outInLogger").info(msg);
  }
  
  /**短信模块的Logger*/
  public static void smInfo(String entity) {
      Logger.getLogger("smLogger").info(entity);
  }
  
  /**消息队列模块的Logger*/
  public static void messageInfo(Object entity) {
      Logger.getLogger("messageLogger").info(entity);
  }
  
  /**批处理模块的Logger*/
  public static void batchProcessInfo(String entity) {
      Logger.getLogger("batchProcessLogger").info(entity);
  }
  

  /** 缓存存取的Logger */
  public static void cacheInfo(Object msg) {
    Logger.getLogger("cacheLogger").info(msg);
  }
  
  /** 缓存存取的Logger --记录异常信息 */
  public static void cacheInfo(Object msg, Throwable throwable) {
    Logger.getLogger("cacheLogger").info(msg);
    error(msg.toString(), throwable);
  }


  /** 需人工干预的报警 - 记alarm日志的报警 */
  @Deprecated
  public static void alarmInfo(String msg) {
    Logger.getLogger("alarmLogger").info(msg);
  }
  
  public static void printAlarm(Logger logger, String msg, Exception e) {
    if (msg != null) {
      logger.info(msg);
    }
    if (e != null) {
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      e.printStackTrace(new PrintWriter(buf, true));
      logger.error(buf.toString());
    }
  }

  /** errorLogger */
  public static void error(String msg, Exception e) {
    printError(Logger.getLogger("errorLogger"), msg, e);
  }

  public static void printError(Logger logger, String msg, Exception e) {
    if (msg != null) {
      logger.error(msg);
    }
    if (e != null) {
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      e.printStackTrace(new PrintWriter(buf, true));
      logger.error(buf.toString());
    }
  }

  /** errorLogger */
  public static void error(String msg, Throwable e) {

    printError(Logger.getLogger("errorLogger"), msg, e);
  }

  public static void printError(Logger logger, String msg, Throwable e) {
    if (msg != null) {
      logger.error(msg);
    }
    if (e != null) {
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      e.printStackTrace(new PrintWriter(buf, true));
      logger.error(buf.toString());
    }
  }

}
