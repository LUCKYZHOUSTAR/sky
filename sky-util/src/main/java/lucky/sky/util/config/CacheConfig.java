package lucky.sky.util.config;

import lombok.Getter;
import lucky.sky.util.convert.StringConverter;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.time.Duration;
import java.util.HashMap;


public final class CacheConfig {

  private static final int DEFAULT_CACHE_TIME = 30;

  private CacheConfig() {
  }

  @Getter
  private String provider = "redis";
  @Getter
  private boolean enabled = true;
  @Getter
  private boolean allowKeyMissing = false;
  @Getter
  private Duration defaultTime;
  @Getter
  private HashMap<String, CacheInfo> infos = new HashMap<>();
  protected static final Logger logger = LoggerManager.getLogger(CacheConfig.class);
//    @Getter
//    private SettingMap settings = new SettingMap();

  /**
   * 获取默认的缓存配置
   *
   * @return 缓存配置
   */
  public static CacheConfig getDefault() {
    return Holder.defaultInstance;
  }

  public static CacheConfig newInstance(String filePath) {
    try {
      Document doc = ConfigParser.resolveXml(filePath);
      return newInstance(doc);
    } catch (Exception e) {
      throw new ConfigException(e);
    }
  }

  public static CacheConfig newInstanceXml(String xml) {
    Document doc = ConfigParser.resolveXmlDoc(xml);
    return newInstance(doc);
  }

  public static CacheConfig newInstance() {
    //AppConfig.getDefault().getShare()
    //RemoteLoader.load("cache.mx_admin_portal.config");
    return null;
  }

  private static CacheConfig newInstance(Document doc) {
    //<caches provider="memory">
    //    <cache key="GetGroup" time="10" versionKey="GetGroupVersion"/>
    //</caches>
    CacheConfig cfg = new CacheConfig();

    org.w3c.dom.Element elem = doc.getDocumentElement();
//        NamedNodeMap attributes = elem.getAttributes();
//        SettingMap settings = new SettingMap();
//        for (int i=0; i<attributes.getLength(); i++) {
//            Node item = attributes.item(i);
//            cfg.settings.put(item.getLocalName(), item.getNodeValue());
//        }
    String provider = elem.getAttribute("provider");
    String enabled = elem.getAttribute("enabled");
    String allowKeyMissing = elem.getAttribute("allowKeyMissing");
    String defaultTimeString = elem.getAttribute("defaultTime");
    if (!StrKit.isBlank(provider)) {
      cfg.provider = provider;
    }
    if (!StrKit.isBlank(enabled)) {
      cfg.enabled = StringConverter.toBool(enabled, true);
    }
    if (!StrKit.isBlank(allowKeyMissing)) {
      cfg.allowKeyMissing = StringConverter.toBool(allowKeyMissing, false);
    }
    int defaultTime = StrKit.isBlank(defaultTimeString) ? DEFAULT_CACHE_TIME
        : StringConverter.toInt32(defaultTimeString, DEFAULT_CACHE_TIME);
    cfg.defaultTime = Duration.ofMinutes(defaultTime);

    NodeList nodes = doc.getElementsByTagName("cache");
    for (int i = 0; i < nodes.getLength(); i++) {
      org.w3c.dom.Element cacheElem = (org.w3c.dom.Element) nodes.item(i);
      String key = cacheElem.getAttribute("key");
      String versionKey = cacheElem.getAttribute("versionKey");
      String timeString = cacheElem.getAttribute("time");
      int time = StrKit.isBlank(timeString) ? defaultTime : Integer.parseInt(timeString);
      CacheInfo ci = new CacheInfo(key, versionKey, time);
      cfg.infos.put(ci.key, ci);
    }

    return cfg;
  }

  @Getter
  public static class CacheInfo {

    private String key;
    private String versionKey;
    /**
     * 缓存时间, 单位分钟
     */
    private Duration time;

    public CacheInfo(String key, String versionKey, int minutes) {
      this.key = key;
      this.versionKey = versionKey;
      this.time = Duration.ofMinutes((minutes == 0) ? 30 : minutes);
    }
  }

  private static class Holder {

    private static CacheConfig defaultInstance;

    static {
      String filePath = ConfigManager.findConfigPath("cache", ".conf", ".xml");
      if (filePath == null) {
        defaultInstance = new CacheConfig();
      } else {
        try {
          defaultInstance = CacheConfig.newInstance(filePath);
        } catch (ConfigException e) {
          logger.error("load config failed", e);
        }
      }
    }
  }
}
