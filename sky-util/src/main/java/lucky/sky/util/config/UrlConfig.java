package lucky.sky.util.config;

import lucky.sky.util.convert.StringConverter;
import lucky.sky.util.lang.XmlHelper;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class UrlConfig {

  private static final Logger logger = LoggerManager.getLogger(UrlConfig.class);
  private HashMap<String, UrlTemplate> urls = new HashMap<>();

  private UrlConfig(HashMap<String, UrlTemplate> urls) {
    this.urls = urls;
  }

  public static UrlConfig getDefault() {
    return Holder.defaultInstance;
  }

  public String getUrl(String key, Object... args) {
    UrlTemplate template = urls.get(key);
    if (template == null) {
      return null;
    }

    return template.transform(args);
  }

  public static UrlConfig newInstance(String serverFile, String urlFile) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      HashMap<String, String> servers;
      if (serverFile == null) {
        servers = new HashMap<>();
      } else {
        Document doc = builder.parse(serverFile);
        servers = loadServerConfig(doc);
      }

      Document doc = builder.parse(urlFile);
      HashMap<String, UrlTemplate> urls = loadConfig(doc, servers);

      return new UrlConfig(urls);
    } catch (Exception e) {
      throw new ConfigException(e);
    }
  }

  private static HashMap<String, UrlTemplate> loadConfig(Document doc,
      HashMap<String, String> servers) {
    //<urls version="2.0">
    //    <server name="www">
    //        <url name="DisneyDVD" path="/Disney/DVD/{0}"/>
    //        <url name="SonyDVD" path="/Sony/DVD/{0}"/>
    //        <url name="ShowTimeMovie" path="/showtime/{0}/"/>
    //    </server>
    //    <server>
    //        <!--IMDB电影URL-->
    //        <url name="IMDBMovie" path="http://www.imdb.com/title/tt{0}/"/>
    //    </server>
    //</urls>
    HashMap<String, UrlTemplate> urls = new HashMap<>();

    NodeList nodes = doc.getElementsByTagName("server");
    for (int i = 0; i < nodes.getLength(); i++) {
      org.w3c.dom.Element elem = (org.w3c.dom.Element) nodes.item(i);
      String name = elem.getAttribute("name");
      String server = null;
      if (name != null && !name.isEmpty()) {
        server = servers.get(name);
        if (server == null) {
          throw new ConfigException("找不到server: " + name);
        }
      }

      List<Element> elements = XmlHelper.getChildElements(elem, "url");
      for (Element xe : elements) {
        String key = xe.getAttribute("name");
        String path = xe.getAttribute("path");
        UrlTemplate template = new UrlTemplate(key, StringConverter.nullToEmpty(server) + path);
        urls.put(key, template);
      }
    }

    return urls;
  }

  private static HashMap<String, String> loadServerConfig(Document doc) {
    //<servers>
    //    <server name="www" url="http://www.test.com"/>
    //    <server name="movie" url="http://movie.test.com"/>
    //</servers>
    HashMap<String, String> servers = new HashMap<>();

    NodeList nodes = doc.getElementsByTagName("server");
    for (int i = 0; i < nodes.getLength(); i++) {
      org.w3c.dom.Element elem = (org.w3c.dom.Element) nodes.item(i);
      String name = elem.getAttribute("name");
      String url = elem.getAttribute("url");
      servers.put(name, url);
    }

    return servers;
  }

  static class UrlTemplate {

    private static Pattern regex = Pattern.compile("\\{\\d+}");
    private String key;
    private List<UrlPart> parts = new ArrayList<>();

    UrlTemplate(String key, String url) {
      this.key = key;
      Matcher m = regex.matcher(url);
      int start = 0;
      while (m.find()) {
        if (m.start() > start) {
          parts.add(new UrlPart(url.substring(start, m.start())));
        }

        int index = StringConverter.toInt32(url.substring(m.start() + 1, m.end() - 1));
        parts.add(new UrlPart(index));

        start = m.end();
      }
      if (start < url.length()) {
        parts.add(new UrlPart(url.substring(start)));
      }
    }

    String transform(Object... args) {
      try {
        String[] arr = new String[parts.size()];
        for (int i = 0; i < arr.length; i++) {
          UrlPart part = parts.get(i);
          arr[i] = part.index == -1 ? part.fragment : args[part.index].toString();
        }
        return String.join("", arr);
      } catch (Exception e) {
        String msg = String.format("格式化 URL 出错, key: %s, args: %s", key, Arrays.toString(args));
        throw new IllegalArgumentException(msg, e);
      }
    }
  }

  static class UrlPart {

    // 参数索引
    private int index = -1;
    // URL 片段
    private String fragment;

    UrlPart(int index) {
      this.index = index;
    }

    UrlPart(String fragment) {
      this.fragment = fragment;
    }
  }

  private static class Holder {

    private static UrlConfig defaultInstance;

    static {
      // urlserver 可以忽略
      String serverFile = ConfigManager.findConfigPath("urlserver", ".conf", ".xml");
      String urlFile = ConfigManager.findConfigPath("url", ".conf", ".xml");
      if (urlFile == null) {
        throw new ConfigException("url.conf can't be found");
      }

      try {
        defaultInstance = UrlConfig.newInstance(serverFile, urlFile);
      } catch (ConfigException e) {
        logger.error("load config failed", e);
      }
    }
  }
}
