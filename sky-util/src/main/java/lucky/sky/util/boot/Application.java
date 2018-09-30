package lucky.sky.util.boot;

import lucky.sky.util.build.BuildInfo;
import lucky.sky.util.build.BuildUtil;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.config.ConfigManager;
import lucky.sky.util.config.ConfigProperties;
import lucky.sky.util.convert.DateConverter;
import lucky.sky.util.encode.JsonEncoder;
import lucky.sky.util.etcd.EtcdManager;
import lucky.sky.util.etcd.ZkManager;
import lucky.sky.util.ioc.ServiceLocatorAutoConfig;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.lang.UncheckedException;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import lucky.sky.util.mx.MXUtil;
import lucky.sky.util.thread.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

/**
 * 应用程序启动类
 */
public class Application {

  protected Class<?> bootClass;
  protected Logger logger;
  protected SpringApplication app;
  protected ApplicationContext ctx;
  protected boolean reportEnabled = true;
  String[] args;
  LocalDateTime startTime;

  /**
   * @param bootClass 本程序的 Spring Boot 入口配置类，一般由 @SpringBootApplication 注解。
   */
  public Application(Class<?> bootClass) {
    this(bootClass, new String[0]);
  }

  /**
   * @param bootClass 本程序的 Spring Boot 入口配置类，一般由 @SpringBootApplication 注解。
   * @param args 命令行参数, 不能为空
   */
  public Application(Class<?> bootClass, String[] args) {
    Objects.requireNonNull(bootClass, "bootClass can't be null");
    Objects.requireNonNull(args, "args can't be null");

    this.bootClass = bootClass;
    this.args = args;
    this.app = new SpringApplication(ServiceLocatorAutoConfig.class, bootClass);
    this.logger = LoggerManager.getLogger(getClass());

    this.initialize();
  }

  /**
   * initialize settings, triggered before spring application run
   */
  protected void initialize() {
    // load default spring config
    String path = ConfigManager.findConfigPath("spring", ".conf", ".xml");
    if (path != null) {
      setSources("file:" + path);
    }

    // set default properties
    Map<String, Object> defaultProps = new HashMap<>();
    initDefaultProperties(defaultProps);
    if (defaultProps.size() > 0) {
      app.setDefaultProperties(defaultProps);
    }
  }

  protected void initDefaultProperties(Map<String, Object> props) {
    // hidden welcome banner
    props.put("spring.main.show-banner", false);
    // profile
    String profile = ConfigProperties.activeProfiles();
    if (StrKit.notBlank(profile)) {
      props.put("spring.profiles.active", profile);
    }
    // TODO classpath:/etc/spring.boot.properties:deprecated, retain for compatible, remove this version
    // TODO to extract to ConfigManager
    props.put("spring.config.name", "app");
    props.put("spring.config.location",
        MessageFormat.format("{0}spring.boot.properties,{0}", ConfigManager.getConfigDir()));
  }


  protected void load() {
  }

  /**
   * 设置程序配置源
   */
  public void setSources(Object... sources) {
    if (sources == null) {
      throw new UncheckedException("sources can not be null");
    }

    Set<Object> set = new HashSet<>(sources.length);
    for (Object src : sources) {
      set.add(src);
    }
    app.setSources(set);
  }

  public <T> T getBean(Class<T> clazz) {
    return getBean(clazz, true);
  }

  public <T> T getBean(Class<T> clazz, boolean autowire) {
    if (ctx == null) {
      throw new UncheckedException("you must wait for application running");
    }

    T bean = ctx.getBean(clazz);
    if (bean == null && !clazz.isInterface()) {
      try {
        bean = clazz.newInstance();
        if (autowire) {
          ctx.getAutowireCapableBeanFactory().autowireBean(bean);
        }
      } catch (Exception e) {
        String error = String.format("create bean instance of [%s] failed", clazz.getName());
        throw new UncheckedException(error, e);
      }
    }
    return bean;
  }

  public ApplicationContext run() {
    startTime = LocalDateTime.now();
    ZkManager.init();
    logger.info("zookeeper started");
    ctx = app.run(this.args);
    this.load();
    logger.info("Application started.");
    return ctx;
  }

  public ApplicationContext run(boolean registered) {
    startTime = LocalDateTime.now();
    if (registered) {
      ZkManager.init();
    }
    logger.info("zookeeper started");
    ctx = app.run(this.args);
    this.load();
    logger.info("Application started.");
    return ctx;
  }

  public void run(Consumer<ApplicationContext> action) {
    action.accept(run());
  }


  protected void initMonitor(HttpMonitor monitor) {
  }


}
