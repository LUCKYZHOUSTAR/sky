package lucky.sky.util.config;

import lucky.sky.util.lang.XmlHelper;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.log.ConsoleLogger;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ConfigParser {

  /**
   * 解析 xml 配置文件，如果有动态属性（格式 ${propertyname}) 则进行替换
   */
  public static Document resolveXml(String path) {
    String xml = resolveText(path);
    Document doc = XmlHelper.loadXml(xml);
    return doc;
  }

  public static Document resolveXmlDoc(String xml) {
    Objects.requireNonNull(xml, "arg xml");
    String tmp = resolveProperties(xml);
    Document doc = XmlHelper.loadXml(tmp);
    return doc;
  }

  /**
   * 对纯文本文件中 ${propertyname} 进行替换
   */
  public static String resolveText(String path) {
    StringBuilder sb = new StringBuilder();
    ConsoleLogger.debug("config > resolve config %s", path);

    Properties propSource = ConfigProperties.getActiveProfileProperties();
    try (InputStream stream = new FileInputStream(path)) {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(stream, StandardCharsets.UTF_8));
      while (true) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        //
        line = resolvePropertiesCore(propSource, line);
        //
        sb.append(line);
        sb.append(StrKit.newLine);
      }
    } catch (Exception ex) {
      throw new ConfigException(ex);
    }

    return sb.toString();
  }


  /**
   * 返回使用当前 profile 配置属性解析后的文本。
   */
  public static String resolveProperties(String source) {
    Objects.requireNonNull(source, "arg source");
    Properties propSource = ConfigProperties.getActiveProfileProperties();
    return resolvePropertiesCore(propSource, source);
  }

  private static String resolvePropertiesCore(Properties propSource, String source) {
    Pattern pattern = Pattern.compile("\\$\\{(?<prop>[^\\s\\{\\}]+)\\}");
    Matcher matcher = pattern.matcher(source);
    List<String> props = new ArrayList<>();
    while (matcher.find()) {
      String prop = matcher.group("prop");
      props.add(prop);
      ConsoleLogger.debug("config > found property place holders: '%s'", prop);
    }

    String resolvedSource = source;
    for (String p : props) {
      String v = propSource.getProperty(p);
      if (v != null) {
        String placeHolder = String.format("${%s}", p);
        resolvedSource = resolvedSource.replace(placeHolder, v);
      }
      ConsoleLogger.debug("config > property place holders '%s' = %s", p, v);
    }
    return resolvedSource;
  }
}
