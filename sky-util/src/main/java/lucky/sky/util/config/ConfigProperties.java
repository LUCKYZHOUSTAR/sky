package lucky.sky.util.config;

import java.security.Key;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.log.ConsoleLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigProperties {

  private static final String activeProfileSeparator = ",";
  private static final String vm_opt_activeProfile = "lucky.profiles.active";
  private static final String lnx_env_activeProfile = vm_opt_activeProfile.replace('.', '_');
  private static final String lnx_env_activeProfile_upper = lnx_env_activeProfile.toUpperCase();
  private static String[] activeProfiles;
  private static Properties activeProps;

  /**
   * 获取属性值
   */
  public static String getProperty(String name) {
    Properties props = getActiveProfileProperties();
    return props.getProperty(name);
  }

  /**
   * @param defaultValue 未配置的缺省值
   */
  public static String getProperty(String name, String defaultValue) {
    Properties props = getActiveProfileProperties();
    return props.getProperty(name, defaultValue);
  }

  /**
   * 获取不可空属性，如果未配置，则抛出 IllegalStateException
   */
  public static String getRequiredProperty(String name) {
    String v = getProperty(name);
    if (name == null) {
      throw new IllegalStateException("not found property " + name);
    }
    return v;
  }

  /**
   * 获取当前环境属性列表。
   */
  public static Properties getActiveProfileProperties() {
    if (activeProps == null) {
      synchronized (ConfigProperties.class) {
        if (activeProps == null) {
          Properties tmpProps = new Properties();
          String[] activeProfiles = getActiveProfiles();
          // 全局缺省配置
          String defaultGlobalPropsPath = getDefaultGlobalPropertiesPath();
          Properties defaultGlobalProps = loadProps(defaultGlobalPropsPath);
          merge(tmpProps, defaultGlobalProps);
          // 全局特定 profile 配置
          for (String p : activeProfiles) {
            String activeGlobalPropsPath = getActiveGlobalPropertiesPath(p);
            Properties activeGlobalProps = loadProps(activeGlobalPropsPath);
            merge(tmpProps, activeGlobalProps);
          }
          // 当前程序默认配置，由程序编码提供
          Properties defaultProps = DefaultConfigProperties.getProperties();
          if (defaultProps.size() > 0) {
            merge(tmpProps, defaultProps);
          }
          // 当前程序缺省 profile 配置
          String defaultAppPropsPath = getDefaultAppPropertiesPath();
          Properties defaultAppProps = loadProps(defaultAppPropsPath);
          merge(tmpProps, defaultAppProps);
          // 当前程序特定 profile 配置
          for (String p : activeProfiles) {
            String activeAppPropsPath = getActiveAppPropertiesPath(p);
            Properties activeAppProps = loadProps(activeAppPropsPath);
            merge(tmpProps, activeAppProps);
          }
          //
          activeProps = tmpProps;
        }
      }
    }
    return activeProps;
  }

  private static void merge(Properties props1, Properties props2) {
    if (props2 != null && props2.size() > 0) {
      props1.putAll(props2);
    }
  }

  /**
   * 加载 properties 文件，如果文件不存在，则返回空 Properties 对象。
   */
  public static Properties loadProperties(String path) {
    return loadProps(path);
  }

  private static Properties loadProps(String path) {
    Properties props = new Properties();
    File file = new File(path);
    if (!file.exists()) {
      ConsoleLogger.debug("config > not found %s", path);
      return props;
    }

    ConsoleLogger.debug("found %s:", path);
    try (FileInputStream globalPropsInputStream = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(globalPropsInputStream,
            StandardCharsets.UTF_8)) {
      props.load(reader);
      if (ConsoleLogger.isDebugEnabled()) {
        ConsoleLogger.debug("config > load properties:", path);
        for (String k : props.stringPropertyNames()) {
          ConsoleLogger.debug("\t\t%s=%s", k, props.get(k));
        }
      }
    } catch (Exception ex) {
      throw new ConfigException(ex);
    }
    return props;
  }

  private static String getDefaultAppPropertiesPath() {
    return ConfigManager.getConfigPath("app.properties");
  }

  private static String getActiveAppPropertiesPath(String profile) {
    return ConfigManager.getConfigPath(String.format("app-%s.properties", profile));
  }

  private static String getDefaultGlobalPropertiesPath() {
    return getGlobalConfigPath("global.properties");
  }

  private static String getActiveGlobalPropertiesPath(String profile) {
    return getGlobalConfigPath(String.format("global-%s.properties", profile));
  }

  private static String getGlobalConfigPath(String fileName) {
    return ConfigManager.getGlobalConfigDir() + fileName;
  }

  /**
   * 获取当前激活的环境配置
   */
  public synchronized static String[] getActiveProfiles() {
    if (activeProfiles == null) {
      activeProfiles = searchActiveProfiles();
    }
    return activeProfiles;
  }

  private static String[] searchActiveProfiles() {

    String[] profiles = getActiveProfiles(vm_opt_activeProfile, true);
    if (profiles != null) {
      return profiles;
    }


    // then, find from environment variables
    // *nix env cant contain dot
    profiles = getActiveProfiles(lnx_env_activeProfile_upper, false);
    if (profiles == null) {
      profiles = getActiveProfiles(lnx_env_activeProfile, false);
      if (profiles == null) {
        profiles = getActiveProfiles(vm_opt_activeProfile, false);
        if (profiles == null) {
          // not found anywhere
          profiles = new String[0];
        }
      }
    }
    return profiles;
  }

  private static String[] getActiveProfiles(String p, boolean vmOrEnv) {
    String profiles = vmOrEnv ? System.getProperty(p) : System.getenv(p);
    String where = vmOrEnv ? "vm options" : "env variables";
    if (StrKit.notBlank(profiles)) {
      ConsoleLogger.debug("config > found %s: %s in %s", p, profiles, where);
      return profiles.split(activeProfileSeparator);
    }
    ConsoleLogger.debug("config > not found %s in %s", p, where);
    return null;
  }

  /**
   * 以逗号分隔的多个环境配置名称
   */

  public static String activeProfiles() {
    String[] profiles = getActiveProfiles();
    if (profiles == null || profiles.length == 0) {
      return "";
    }
    return String.join(activeProfileSeparator, getActiveProfiles());
  }
}
