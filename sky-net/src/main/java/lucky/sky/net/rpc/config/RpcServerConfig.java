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
public class RpcServerConfig {

  private static final Logger logger = LoggerManager.getLogger(RpcServerConfig.class);
  private static final Map<String, ServerOptions> optionsMap = new HashMap<>();

  static {
    String filePath = ConfigManager.findConfigPath("remote.server", ".conf", ".xml");
    if (filePath != null) {
      try {
        load(filePath);
      } catch (ConfigException e) {
        logger.error("load config failed", e);
      }
    }
  }

  private RpcServerConfig() {
    // 防止实例化
  }

  public static ServerOptions get(String name) {
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
    NodeList nodes = doc.getElementsByTagName("server");
    for (int i = 0; i < nodes.getLength(); i++) {
      Element elem = (Element) nodes.item(i);

      ServerOptions options = new ServerOptions();
      options.setName(XmlHelper.getAttribute(elem, "name", DefaultConfigProperties.getAppName()));
      options.setType(XmlHelper.getAttribute(elem, "type", "simple"));
      options.setVersion(elem.getAttribute("version"));
      options.setAddress(elem.getAttribute("address"));
      options.setRegister(StringConverter.toBool(elem.getAttribute("register"),
          AppConfig.getDefault().getGlobal().isRpcRegisterEnabled()));
      options.setDescription(elem.getAttribute("description"));

      SettingMap settings = XmlHelper.toSettingMap(elem.getChildNodes(), "name", "value");
      options.setSettings(settings);

      optionsMap.put(options.getName(), options);
    }
  }
}
