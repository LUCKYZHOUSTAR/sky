package lucky.sky.util.test.spring;

import lucky.sky.util.lang.StrKit;
import lucky.sky.util.config.ConfigManager;
import lucky.sky.util.ioc.SpringServiceLocator;
import lucky.sky.util.lang.UncheckedException;
import lucky.sky.util.log.ConsoleLogger;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.springframework.boot.SpringApplication;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Properties;

/**
 * 基于 Spring 的 JUnit 辅助工具。
 */
public class SpringJUnit {

  private static final Logger log = LoggerManager.getLogger(SpringJUnit.class);

  /**
   * @param configClass
   */
  public static void boot(Class configClass) {
    boot(configClass, configClass);
  }

  /**
   * 启动 Spring Boot Application。
   *
   * @param configClassInLocalTest 单元测试项目中的 @SpringBootApplication 类，用于获取单元测试项目的配置；
   * 注意不要是使用任何测试类本身，会导致 Spring Boot 陷入死循环。
   * @param classInServiceImpl 远程服务实现项目中的 @SpringBootApplication 类，用于获取远程服务实现项目的配置，实现本地调用服务。
   */
  public static void boot(Class configClassInLocalTest, Class classInServiceImpl) {
    Objects.requireNonNull(configClassInLocalTest, "arg configClassInLocalTest");

    //
    boolean remoting = isRemoting(
        classInServiceImpl == null ? configClassInLocalTest : classInServiceImpl);
    Class configClass = remoting ? configClassInLocalTest : classInServiceImpl;
    ConsoleLogger.info("testing " + (remoting ? "remoting" : "local"));
    //
    if (!remoting && classInServiceImpl == null) {
      throw new IllegalArgumentException(
          "intend to invoke local service impl but classInServiceImpl is null");
    }

    String configPath = getConfigDir(remoting ? configClassInLocalTest : classInServiceImpl);
    ConsoleLogger.info("configDir " + configPath);
    ConfigManager.setConfigDir(configPath);
    //
    Properties props = new Properties();
    props.put("spring.main.show-banner", false);
    props.put("spring.main.web_environment", false);

    System.setProperty("spring.config.location",
        MessageFormat.format("{0}spring.boot.properties,{0}", configPath));
    System.setProperty("spring.config.name", "app");

    SpringApplication app;
    String xmlFilePath = ConfigManager.findConfigPath("spring", ".conf", ".xml");
    if (xmlFilePath != null) {
      log.info("found spring config: " + xmlFilePath);
      app = new SpringApplication("file:" + xmlFilePath, configClass);
    } else {
      log.info("not found spring root xml " + xmlFilePath);
      app = new SpringApplication(configClass);
    }

    app.setDefaultProperties(props);
    //
    app.addInitializers(ctx -> {
      // TODO workaround, duplicate with AutoConfig, to refine
      SpringServiceLocator l = new SpringServiceLocator();
      l.setApplicationContext(ctx);
    });
    app.run();
  }

  public static boolean isRemoting(Class clazz) {
    final String longFlag = "mtime.testing.remoting";
    final String shortFlag = "r";
    String r = System.getenv(longFlag);
    if (StrKit.isBlank(r)) {
      r = System.getenv(shortFlag);
    }
    if (StrKit.isBlank(r)) {
      // ..../target/classes/
      String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
      // 基于 maven 测试项目目录约定
      path += "../../src/test/resources/etc/app.properties";
      File file = new File(path);
      if (file.exists()) {
        try (FileInputStream globalPropsInputStream = new FileInputStream(file)) {
          System.getProperties().load(globalPropsInputStream);
        } catch (Exception ex) {
          throw new UncheckedException(ex);
        }
      }
      r = System.getProperty(longFlag);
    }

    return !StrKit.isBlank(r) && Boolean.parseBoolean(r);
  }

  private static String getConfigDir(Class clazz) {
    return clazz.getProtectionDomain().getCodeSource().getLocation().getPath() + "etc/";
  }

}

