package lucky.sky.util.config;

import java.util.HashMap;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.lang.XmlHelper;
import lucky.sky.util.log.ConsoleLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class AppConfig {

  public static AppConfig getDefault() {
    return Holder.defaultInstance;
  }


  private String appName;

  public String appName() {
    return this.appName;
  }

  public String getAppName() {
    return this.appName;
  }

  private HashMap<String, SettingMap> sections = new HashMap<>();

  private String globalEnv = "";

  private GlobalConfig global;

  private AppConfig() {
    this.appName = ConfigProperties.getProperty("server.name");
    if (StrKit.isBlank(appName)) {
      throw new IllegalArgumentException("server name can not be null");
    }
    this.initGlobalConfig();
  }


  public AppConfig(Document doc) {
    loadConfig(doc);
    this.initGlobalConfig();
  }

  /**
   * 获取全局配置
   */
  public GlobalConfig getGlobal() {
    return this.global;
  }

  public SettingMap getSection(String name) {
    return sections.get(name);
  }


  private void loadConfig(Document doc) {
    NodeList nodes = doc.getDocumentElement().getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      org.w3c.dom.Node node = nodes.item(i);
      if (node.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }

      org.w3c.dom.Element elem = (org.w3c.dom.Element) node;
      parseSection(elem);
    }
  }


  private void initGlobalConfig() {
    SettingMap primary = this.getSection("global");
    SettingMap standby = null;

    String filePath = null;
    String globalEnvFromJvmOpt = getGlobalEnvFromJvmOpt();
    if (!StrKit.isBlank(globalEnvFromJvmOpt)) {
      ConsoleLogger
          .info("config > try to find global config by JVM option as %s", globalEnvFromJvmOpt);
      filePath = ConfigManager
          .findGlobalConfigPath("global." + globalEnvFromJvmOpt, ".conf", ".xml");
    }
    if (filePath == null && !StrKit.isBlank(this.globalEnv)) {
      ConsoleLogger
          .info("config > try to f`  ind global config by app.config as %s", globalEnvFromJvmOpt);
      filePath = ConfigManager.findGlobalConfigPath("global." + this.globalEnv, ".conf", ".xml");
    }
    if (filePath == null) {
      filePath = ConfigManager.findGlobalConfigPath("global", ".conf", ".xml");
    }

    if (filePath == null) {
      ConsoleLogger.info("config > Warning: global config can't be found");
    } else {
      ConsoleLogger.info("config > load global config: %s", filePath);
      try {
        Document doc = XmlHelper.loadDocument(filePath);
        NodeList nodes = doc.getElementsByTagName("setting");
        standby = XmlHelper.toSettingMap(nodes, "key", "value");
      } catch (Exception e) {
        throw new ConfigException(e);
      }
    }

    this.global = new GlobalConfig(SettingMap.merge(primary, standby));
  }

  /**
   * -Dlucky.global_env
   */
  private String getGlobalEnvFromJvmOpt() {
    return System.getProperty("lucky.global_env");
  }

  private void parseSection(org.w3c.dom.Element elem) {
    NodeList nodes = elem.getElementsByTagName("add");
    String name = elem.getTagName();
    SettingMap settings = XmlHelper.toSettingMap(nodes, "key", "value");
    this.sections.put(name, settings);
  }


  private static class Holder {

    private static AppConfig defaultInstance = new AppConfig();
  }

}
