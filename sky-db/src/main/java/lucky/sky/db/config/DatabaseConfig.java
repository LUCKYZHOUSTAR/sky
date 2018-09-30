package lucky.sky.db.config;

import lombok.Getter;
import lombok.Setter;
import lucky.sky.util.config.*;
import lucky.sky.util.encode.JsonEncoder;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.lang.XmlHelper;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;


public final class DatabaseConfig {

  private DatabaseConfig() {
  }

  private static final Logger logger = LoggerManager.getLogger(DatabaseConfig.class);
  private static HashMap<String, SqlDBInfo> infos = new HashMap<>();

  static {
    loadConfig();
  }

  private static void loadConfig() {
    String filePath = ConfigManager.findConfigPath("db.sql", ".conf", ".xml");
    if (filePath == null) {
      return;
    }

    Document doc = ConfigParser.resolveXml(filePath);
    NodeList nodes = doc.getElementsByTagName("database");
    for (int i = 0; i < nodes.getLength(); i++) {
      Element elem = (Element) nodes.item(i);
      SqlDBInfoImp di = new SqlDBInfoImp();
      di.name = elem.getAttribute("name");
      di.type = elem.getAttribute("provider");
      di.driver = elem.getAttribute("driver");
      di.url = elem.getAttribute("address");

      NodeList settingNodes = elem.getElementsByTagName("setting");
      di.settings = XmlHelper.toSettingMap(settingNodes, "name", "value");

      if (StrKit.isBlank(di.url)) {
        di.url = di.settings.getString("ConnString");
      }
      di.normalizeUrl();
      di.user = di.settings.getString("Username");
      di.pwd = di.settings.getString("Password");

      infos.put(di.name, di);
    }
  }

  /**
   * @throws ConfigException if config can't be found
   */
  public synchronized static SqlDBInfo get(String name) {
    SqlDBInfo dbInfo = infos.get(name);
    if (dbInfo == null) {
      dbInfo = getRemote(name);
      if (dbInfo == null) {
        throw new ConfigException("can't find db config for " + name);
      }
    }
    return dbInfo;
  }

  /**
   * 从配置中心获取配置
   */
  private static SqlDBInfo getRemote(String name) {
    HashMap<String, String> args = new HashMap<>();
    args.put("name", name);
    args.put("app", AppConfig.getDefault().getAppName());
    args.put("host", AppConfig.getDefault().getGlobal().getRpcRegisterIP());
    RemoteLoader.Result result = RemoteLoader.load("/db/sql", args);
    if (!result.isSuccess()) {
      logger
          .error("config > db.sql: load remote config of [{}] failed: {}", name, result.getError());
      return null;
    }

    SqlDBInfoImp info = JsonEncoder.DEFAULT.decode(result.getValue(), SqlDBInfoImp.class);
    info.settings = (info.options == null) ? new SettingMap() : new SettingMap(info.options);
    info.normalizeUrl();

    logger.debug("config > db.sql: load [{}] ok from config center", name);
    infos.put(name, info);
    return info;
  }

  public interface SqlDBInfo {

    String getName();

    String getType();

    String getDriver();

    String getUrl();

    String getUser();

    String getPwd();

    SettingMap getSettings();
  }

  @Getter
  @Setter
  public static class SqlDBInfoImp implements SqlDBInfo {

    private String name;
    private String type;
    private String driver;
    private String url;
    private String user;
    private String pwd;
    private String version;
    private Map<String, String> options;
    private SettingMap settings;

    void normalizeUrl() {
      if (!this.url.startsWith("jdbc:")) {
        this.url = "jdbc:" + this.url;
      }
    }
  }
}
