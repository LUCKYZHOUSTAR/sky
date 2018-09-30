package lucky.sky.util.config;

import lucky.sky.util.convert.StringConverter;

import java.util.Properties;

/**
 * 提供缺省配置属性容器。
 */
public final class DefaultConfigProperties {

  private static Properties props = new Properties();

  public static int getSystemId() {
    return getIntegerOrDefault("system_id");
  }

  public static void setSystemId(int systemId) {
    setProperty("system_id", systemId);
  }

  public static int getAppId() {
    return getIntegerOrDefault("app_id");
  }

  public static void setAppId(int appId) {
    setProperty("app_id", appId);
  }

  public static String getAppName() {
    return (String) getProperty("app_name");
  }

  public static void setAppName(String appName) {
    setProperty("app_name", appName);
  }

  public static void setProperty(String name, Object value) {
    props.put(name, value);
  }

  public static Object getProperty(String name) {
    return props.get(name);
  }

  public static String getString(String name) {
    return (String) props.get(name);
  }

  public static Properties getProperties() {
    return props;
  }

  public static int getIntegerOrDefault(String name) {
    // HACK, to refine
    Object v = getProperty(name);
    return StringConverter.toInt32(String.valueOf(v), 0);
  }
}
