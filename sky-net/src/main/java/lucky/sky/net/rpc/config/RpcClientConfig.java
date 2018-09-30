package lucky.sky.net.rpc.config;

import lucky.sky.util.config.*;
import lucky.sky.util.convert.StringConverter;
import lucky.sky.util.lang.XmlHelper;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class RpcClientConfig {

  private static final Logger logger = LoggerManager.getLogger(RpcClientConfig.class);
  private static final Map<String, ClientOptions> optionsMap = new HashMap<>();

  static {
    String filePath = ConfigManager.findConfigPath("remote.client", ".conf", ".xml");
    if (filePath != null) {
      try {
        load(filePath);
      } catch (ConfigException e) {
        logger.error("load config failed", e);
      }
    }
  }

  private RpcClientConfig() {
    // 防止实例化
  }

  public static ClientOptions get(String name) {
    return optionsMap.get(name);
  }

  /**
   * 创建配置实例
   *
   * @param input 文件流
   */
  public static void load(InputStream input) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(input);
      load(doc);
    } catch (Exception e) {
      throw new ConfigException(e);
    }
  }

  /**
   * 创建配置实例
   *
   * @param filePath 配置文件路径
   */
  public static void load(String filePath) {
    try {
      Document doc = ConfigParser.resolveXml(filePath);
      load(doc);
    } catch (Exception e) {
      throw new ConfigException(e);
    }
  }

  private static void load(Document doc) {
    //<servers>
    //    <server name="ecommerce" type="simple" group="" address="192.168.50.52:15011" discovery="false" description="后产品服务">
    //        <setting name="MaxConnections" value="100"/>
    //    </server>
    //</servers>
    NodeList nodes = doc.getElementsByTagName("server");
    for (int i = 0; i < nodes.getLength(); i++) {
      Element elem = (Element) nodes.item(i);

      ClientOptions options = new ClientOptions();
      options.setName(elem.getAttribute("name"));
      options.setAlias(elem.getAttribute("alias"));
      options.setGroup(elem.getAttribute("group"));
      options.setVersion(elem.getAttribute("version"));
      options.setType(XmlHelper.getAttribute(elem, "type", "simple"));
      options.setAddress(elem.getAttribute("address"));
      options.setDiscovery(StringConverter.toBool(elem.getAttribute("discovery"),
          AppConfig.getDefault().getGlobal().isRpcDiscoveryEnabled()));
      options.setDescription(elem.getAttribute("description"));

      SettingMap settings = XmlHelper.toSettingMap(elem.getChildNodes(), "name", "value");
      options.setSettings(settings);

      optionsMap.put(options.getName(), options);
    }
  }
}
