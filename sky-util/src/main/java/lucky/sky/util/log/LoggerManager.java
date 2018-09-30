package lucky.sky.util.log;

import lucky.sky.util.config.AppConfig;
import lucky.sky.util.config.ConfigManager;
import lucky.sky.util.config.ConfigParser;
import lucky.sky.util.lang.Exceptions;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.apache.log4j.xml.SAXErrorHandler;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Properties;


public class LoggerManager {

  static {
    // todo: sky.log.configure 设置用于解决某些第三方组件提前初始化 log4j 的问题, 暂时没有更好的办法
    if (!isConfigured()) {
      String configPath = ConfigManager.findConfigPath("log4j", ".conf", ".xml");
      if (configPath == null) {
        ConsoleLogger.info(
            "config > log4j.conf/log4j.xml can't be found in [%s], default settings will be used",
            ConfigManager.getConfigDir());
        setDefaultConfig();
      } else {
        try {
          // 替换 profile 属性
          String text = ConfigParser.resolveText(configPath);
          InputSource inputSource = new InputSource(new StringReader(text));
          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          dbf.setValidating(true);
          DocumentBuilder builder = dbf.newDocumentBuilder();
          builder.setErrorHandler(new SAXErrorHandler());
          builder.setEntityResolver(new Log4jEntityResolver());
          Document doc = builder.parse(inputSource);

          // 激活配置
          DOMConfigurator.configure(doc.getDocumentElement());
        } catch (Exception ex) {
          ConsoleLogger
              .warn("config > failed to load log4j.conf at %s: %s-%s", configPath, ex.getMessage(),
                  Exceptions.getStackTrace(ex));
        }
      }
    }
  }

  public static boolean hasLogger(String name) {
    try {
      return org.apache.log4j.LogManager.exists(name) != null;
    } catch (NoSuchMethodError e) {
      // TODO: to dig why not found in some project in fact 1.2.6 existing ?
      ConsoleLogger.warn("not found method: org.apache.log4j.LogManager.exists");
      return false;
    }
  }

  public static Logger getLogger(Class clazz) {
    return new Logger.LoggerImp(org.slf4j.LoggerFactory.getLogger(clazz));
  }

  public static Logger getLogger(String name) {
    return new Logger.LoggerImp(org.slf4j.LoggerFactory.getLogger(name));
  }

  private static boolean isConfigured() {
    Enumeration appenders = org.apache.log4j.Logger.getRootLogger().getAllAppenders();
    if (appenders.hasMoreElements()) {
      return true;
    } else {
      Enumeration loggers = LogManager.getCurrentLoggers();
      while (loggers.hasMoreElements()) {
        org.apache.log4j.Logger c = (org.apache.log4j.Logger) loggers.nextElement();
        if (c.getAllAppenders().hasMoreElements()) {
          return true;
        }
      }
    }
    return false;
  }

  private static void setDefaultConfig() {
    Properties properties = new Properties();

    properties.put("log4j.rootLogger", "DEBUG,C,F");

    properties.put("log4j.appender.C", "org.apache.log4j.ConsoleAppender");
    properties.put("log4j.appender.C.Threshold", "INFO");
    properties.put("log4j.appender.C.layout", "org.apache.log4j.PatternLayout");
    properties.put("log4j.appender.C.layout.ConversionPattern",
        "[%-5p] %d{yyyy-MM-dd HH:mm:ss.SSS} %m%n");

    properties.put("log4j.appender.F", "DailyFileAppender");
    properties.put("log4j.appender.F.Threshold", "INFO");
    properties.put("log4j.appender.F.File", "default.log");
    properties.put("log4j.appender.F.layout", "org.apache.log4j.PatternLayout");
    properties.put("log4j.appender.F.layout.ConversionPattern", "%d{yy-MM-dd HH:mm:ss} %-5p %m%n");

    PropertyConfigurator.configure(properties);
  }
}
