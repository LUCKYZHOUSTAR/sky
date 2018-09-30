package lucky.sky.util.config;

import lucky.sky.util.io.Files;
import lucky.sky.util.io.Path;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.log.ConsoleLogger;

import java.io.File;
import java.net.URL;


public final class ConfigManager {

  private static final String GLOBAL_CONFIG_PATH_OPT_KEY = "lucky.global.config.path";
  private static final String GLOBAL_CONFIG_PATH_ENV_KEY = "lucky_GLOBAL_CONFIG_PATH";
  private static final String vm_opt_configPath = "lucky.config.path";
  private static String configDir;
  private static String globalConfigDir;
  private static String appDir;

  private ConfigManager() {
  }

  /**
   * 设置配置目录  etc  所在路径
   */
  public static void setConfigDir(String configPath) {
    if (configPath == null || configPath.trim().length() == 0) {
      throw new IllegalArgumentException("arg configPath is null or empty");
    }

    // dont take Logger here, bcz it needs the config dir firstly
    // TODO to extract some unified fallback logger
    ConsoleLogger.info("config > change config dir to " + configPath);
    System.setProperty(vm_opt_configPath, configPath);
    configDir = configPath;
  }

  /**
   * 获取 etc 配置目录路径 首次调用将进行将自动进行初始化
   */
  public static String getConfigDir() {
    if (StrKit.isBlank(configDir)) {
      configDir = searchConfigDir();
    }
    return configDir;
  }


  private static String searchConfigDir() {
    String configPath = System.getProperty(vm_opt_configPath);
    if (StrKit.notBlank(configPath)) {
      ConsoleLogger.info("config > found %s: %s", vm_opt_configPath, configPath);
      return configPath;
    }

    ConsoleLogger.info("config > try to resolve etc path by resource url, due to %s null",
        vm_opt_configPath);
    URL etcUrl = Thread.currentThread().getContextClassLoader().getResource("etc");
    if (etcUrl != null) {
      configPath = etcUrl.getPath() + "/";
      ConsoleLogger.info("config > found etc path by resource url: " + configPath);
    } else {
      ConsoleLogger.info("config > try to resolve etc path by code src, due to resource url null");
      String classPath = ConfigManager.class.getProtectionDomain().getCodeSource().getLocation()
          .getPath();
      File file = new File(classPath);
      String filePath = file.getAbsolutePath();
      if (classPath.endsWith(".jar")) {
        filePath = file.getParentFile().getParent();
      }
      /**
       * 作为web项目的时候，特殊处理
       */
      if (filePath.contains("WEB-INF")) {
        configPath = filePath + File.separatorChar + "classes" + File.separatorChar + "etc"
            + File.separatorChar;
      } else {
        configPath = filePath + File.separatorChar + "etc" + File.separatorChar;
      }
      ConsoleLogger.info("config > found etc path by code src: " + configPath);
    }
    System.setProperty(vm_opt_configPath, configPath);
    return configPath;
  }

  static String getGlobalConfigDir() {
    if (globalConfigDir == null) {
      String dir = System.getProperty(GLOBAL_CONFIG_PATH_OPT_KEY);
      if (StrKit.isBlank(dir)) {
        dir = System.getenv(GLOBAL_CONFIG_PATH_ENV_KEY);
        if (!StrKit.isBlank(dir)) {
          ConsoleLogger.info("config > found `%s` in env variables", GLOBAL_CONFIG_PATH_ENV_KEY);
        }
      }
      if (StrKit.isBlank(dir)) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("linux")) {
          dir = "/etc/";
        } else if (os.startsWith("windows")) {
          dir = "D:\\etc\\";
        } else if (os.startsWith("mac os")) {
          dir = "/Users/etc/lucky/";
        } else {
          throw new IllegalStateException("unsupported os: " + os);
        }
      }

      globalConfigDir = dir;
    }
    return globalConfigDir;
  }

  static String findGlobalConfigPath(String filename, String... extensions) {
    if (extensions == null || extensions.length == 0) {
      String path = getGlobalConfigDir() + filename;
      if (Files.exists(path)) {
        return path;
      }
    } else {
      for (String ext : extensions) {
        String path = getGlobalConfigDir() + filename + ext;
        if (Files.exists(path)) {
          return path;
        }
      }
    }

    return null;
  }

  /**
   * 获取指定配置文件在 etc 下的完整路径
   */
  public static String getConfigPath(String filename) {
    return getConfigDir() + filename;
  }

  /**
   * 根据文件名和扩展名获取配置文件路径, 不存在返回 null
   *
   * @param filename 文件名
   * @param extensions 扩展名列表
   */
  public static String findConfigPath(String filename, String... extensions) {
    if (extensions == null || extensions.length == 0) {
      String path = getConfigDir() + filename;
      if (Files.exists(path)) {
        return path;
      }
    } else {
      for (String ext : extensions) {
        String path = getConfigDir() + filename + ext;
        if (Files.exists(path)) {
          return path;
        }
      }
    }

    return null;
  }

  /**
   * 将配置目录设定到当前线程的类加载器所在的 ClassPath。
   *
   * @deprecated 不应该再显示调用此方法，直接使用 getConfigDir 方法即可
   */
  @Deprecated
  public static void setConfigDirToClassPath() {
    ConfigManager.setConfigDir(
        Thread.currentThread().getContextClassLoader().getResource("etc").getPath() + "/");
  }

  /**
   * 获取应用程序运行目录物理路径，并以 / 结尾
   */
  public static String getAppDir() {
    if (appDir == null) {
      // WORKAROUND: just infer from config path, need to refine
      appDir = ConfigManager.getConfigDir();
      appDir = appDir.substring(0, appDir.length() - 4);  // remove /etc
    }
    return appDir;
  }

  /**
   * 获取相对应用程序根目录的完整路径
   *
   * @param path 相对 appPath 的路径
   */
  public static String getFullPath(String path) {
    String appPath = getAppDir();
    return Path.join(appPath, path);
  }
}
