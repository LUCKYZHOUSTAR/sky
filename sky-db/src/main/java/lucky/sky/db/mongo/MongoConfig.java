package lucky.sky.db.mongo;

import lombok.Getter;
import lombok.Setter;
import lucky.sky.util.config.*;
import lucky.sky.util.encode.JsonEncoder;
import lucky.sky.util.lang.XmlHelper;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;


public final class MongoConfig {

  private MongoConfig() {
  }

  private static final Logger logger = LoggerManager.getLogger(MongoConfig.class);
  private static HashMap<String, MongoInfo> infos = new HashMap<>();

  static {
    loadConfig();
  }

  private static void loadConfig() {
    String filePath = ConfigManager.findConfigPath("db.mongo", ".conf", ".xml");
    if (filePath == null) {
      return;
    }

    Document doc = ConfigParser.resolveXml(filePath);
    NodeList nodes = doc.getElementsByTagName("database");
    for (int i = 0; i < nodes.getLength(); i++) {
      Element elem = (Element) nodes.item(i);

      MongoInfoImp info = new MongoInfoImp();
      NodeList settingNodes = elem.getElementsByTagName("setting");
      info.settings = XmlHelper.toSettingMap(settingNodes, "name", "value");
      info.name = elem.getAttribute("name");
      info.url = info.settings.getString("ConnString");
      if (!info.url.startsWith("mongodb://")) {
        info.url = "mongodb://" + info.url;
      }

      infos.put(info.name, info);
    }
  }

  /**
   * @throws ConfigException if config can't be found
   */
  public synchronized static MongoInfo get(String name) {
    MongoInfo info = infos.get(name);
    if (info == null) {
      info = getRemote(name);
      if (info == null) {
        throw new ConfigException("can't find mongo config for " + name);
      }
    }
    return info;
  }

  private static MongoInfo getRemote(String name) {
    HashMap<String, String> args = new HashMap<>();
    args.put("name", name);
    args.put("app", AppConfig.getDefault().getAppName());
    args.put("host", AppConfig.getDefault().getGlobal().getRpcRegisterIP());
    RemoteLoader.Result result = RemoteLoader.load("/db/mongo", args);
    if (!result.isSuccess()) {
      logger.error("config > db.mongo: load remote config of [{}] failed: {}", name,
          result.getError());
      return null;
    }

    MongoInfoImp info = JsonEncoder.DEFAULT.decode(result.getValue(), MongoInfoImp.class);
    info.settings = (info.options == null) ? new SettingMap() : new SettingMap(info.options);
    if (!info.url.startsWith("mongodb://")) {
      info.url = "mongodb://" + info.url;
    }

    logger.debug("config > db.sql: load [{}] ok from config center", name);
    infos.put(name, info);
    return info;
  }

  public interface MongoInfo {

    String getName();

    String getUrl();

    String getVersion();

    SettingMap getSettings();
  }

  @Getter
  @Setter
  static class MongoInfoImp implements MongoInfo {

    private String name;
    private String url;
    private String version;
    private Map<String, String> options;
    private SettingMap settings;
  }

}
