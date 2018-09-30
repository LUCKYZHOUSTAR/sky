package lucky.sky.util.lang;

import com.google.common.base.Strings;
import lucky.sky.util.config.SettingMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class XmlHelper {

  public static Document loadDocument(String filePath) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(filePath);
      return doc;
    } catch (Exception e) {
      throw Exceptions.asUnchecked(e);
    }
  }

  public static Document loadXml(String xml) {
    InputSource inputSource = new InputSource(new StringReader(xml));
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(inputSource);
      return doc;
    } catch (Exception ex) {
      throw new UncheckedException(ex.getMessage(), ex);
    }
  }

  public static Element getChildElement(Element elem, String name) {
    NodeList nodes = elem.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(name)) {
        return (Element) node;
      }
    }
    return null;
  }

  public static List<Element> getChildElements(Element elem, String name) {
    NodeList nodes = elem.getElementsByTagName(name);
    List<Element> elements = new ArrayList<>(nodes.getLength());
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(name)) {
        elements.add((Element) node);
      }
    }
    return elements;
  }

  public static Element getElementByTagName(Element elem, String name) {
    NodeList nodes = elem.getElementsByTagName(name);
    return nodes.getLength() == 0 ? null : (Element) nodes.item(0);
  }

  // todo: 在 1.1 版修改返回值为 List<Element> (不能直接在当前版本修改, 会有兼容问题)
  public static ArrayList<Element> getElementsByTagName(Element elem, String name) {
    NodeList nodes = elem.getElementsByTagName(name);
    ArrayList<Element> elements = new ArrayList<>(nodes.getLength());
    for (int i = 0; i < nodes.getLength(); i++) {
      elements.add((Element) nodes.item(i));
    }
    return elements;
  }

  public static String getAttribute(Element element, String name, String defaultValue) {
    Objects.requireNonNull(element, "arg element");
    String val = element.getAttribute(name);
    if (Strings.isNullOrEmpty(val)) {
      return defaultValue;
    }
    return val;
  }

  public static SettingMap toSettingMap(NodeList nodes, String keyAttr, String valueAttr) {
    SettingMap settings = new SettingMap();
    for (int i = 0; i < nodes.getLength(); i++) {
      if (nodes.item(i).getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }

      org.w3c.dom.Element elem = (org.w3c.dom.Element) nodes.item(i);
      String key = elem.getAttribute(keyAttr);
      String value = elem.getAttribute(valueAttr);
      settings.put(key, value);
    }
    return settings;
  }
}
