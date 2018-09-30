package lucky.sky.db.jsd.template;

import lucky.sky.db.jsd.JsdException;
import lucky.sky.util.config.ConfigManager;
import lucky.sky.util.lang.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/11/18.
 */
public class SqlTemplateManager {

  private static final String DEFAULT_GROUP_NAME = "default";
  private static final HashMap<String, SqlTemplate> TEMPLATES = new HashMap<>();

  static {
    String configPath = ConfigManager.getConfigPath("jsd/template.xml");
    try {
      Document doc = XmlHelper.loadDocument(configPath);
      Element root = doc.getDocumentElement();
      NodeList groupNodes = root.getElementsByTagName("group");
      for (int i = 0; i < groupNodes.getLength(); i++) {
        Node groupNode = groupNodes.item(i);
        loadGroup(groupNode);
      }
    } catch (Exception e) {
      throw new JsdException(e);
    }
  }

  private static void loadGroup(Node node) {
    NodeList sqlNodes = ((Element) node).getElementsByTagName("sql");
    String groupName = ((Element) node).getAttribute("name");
    if (groupName == null || groupName.isEmpty()) {
      throw new NullPointerException("group name attribute is null");
    }

    for (int i = 0; i < sqlNodes.getLength(); i++) {
      Node sqlNode = sqlNodes.item(i);
      String sqlName = ((Element) sqlNode).getAttribute("name");
      if (sqlName == null || sqlName.isEmpty()) {
        throw new NullPointerException("sql name attribute is null");
      }
      SqlTemplate sqlTemplate = loadTemplate(sqlNode);
      String contextKey = getTemplateName(groupName, sqlName);
      if (!TEMPLATES.containsKey(contextKey)) {
        TEMPLATES.put(contextKey, sqlTemplate);
      }
    }
  }

  private static SqlTemplate loadTemplate(Node node) {
    String sqlText = node.getTextContent().trim();
    if (sqlText == null || sqlText.isEmpty()) {
      throw new NullPointerException("sql text content is null");
    }
    return new SimpleTemplate(sqlText);
  }

  public static SqlTemplate get(String group, String name) {
    SqlTemplate sqlTemplate = TEMPLATES.get(getTemplateName(group, name));
    if (sqlTemplate == null) {
      throw new JsdException(String.format("template [%s.%s] does not exist", group, name));
    }

    return sqlTemplate;
  }

  public static SqlTemplate get(String name) {
    return get(DEFAULT_GROUP_NAME, name);
  }

  private static String getTemplateName(String group, String name) {
    return String.join("_", group, name);
  }
}
