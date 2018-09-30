package lucky.sky.util.config;

import lombok.Setter;
import lucky.sky.util.convert.StringConverter;
import lucky.sky.util.encode.JsonEncoder;
import lucky.sky.util.lang.XmlHelper;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class RedisConfig {

  private RedisConfig() {
  }

  private static final Logger logger = LoggerManager.getLogger(RedisConfig.class);
  private static HashMap<String, RedisInfo> infos = new HashMap<>();

  static {
    loadConfig();
  }

  private static void loadConfig() {
    String filePath = ConfigManager.findConfigPath("db.redis", ".conf", ".xml");
    if (filePath == null) {
      return;
    }

    Document doc = ConfigParser.resolveXml(filePath);
    NodeList nodes = doc.getElementsByTagName("database");
    for (int i = 0; i < nodes.getLength(); i++) {
      Element elem = (Element) nodes.item(i);

      RedisInfo info = new RedisInfo();
      info.name = elem.getAttribute("name");

      Element serversElem = XmlHelper.getChildElement(elem, "servers");
      if (serversElem != null) {
        info.servers = new ArrayList<>();
        List<Element> elements = XmlHelper.getChildElements(serversElem, "add");
        for (Element xe : elements) {
          RedisNode redisNode = new RedisNode();
          redisNode.host = xe.getAttribute("host");
          redisNode.port = StringConverter.toInt32(xe.getAttribute("port"));
          info.servers.add(redisNode);
        }
      }

      Element settingsElem = XmlHelper.getChildElement(elem, "settings");
      if (settingsElem != null) {
        info.settings = XmlHelper.toSettingMap(settingsElem.getChildNodes(), "name", "value");
      }

      infos.put(info.name, info);
    }
  }

  public static RedisInfo get(String name) {
    RedisInfo info = infos.get(name);
    if (info == null) {

      throw new IllegalArgumentException(String.format("can't find redis config for [%s]", name));
    }
    return info;
  }

  @Getter
  @Setter
  public static class RedisInfo {

    private String name;
    private String version;
    private String note;
    private List<RedisNode> servers;
    private Map<String, String> options;
    private SettingMap settings;
  }

  @Getter
  @Setter
  public static class RedisNode {

    private String host;
    private int port;
  }
}
