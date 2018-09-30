package lucky.sky.web.velocity.directive;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.Node;

public class Directives {


  public static Object getParam(InternalContextAdapter context, Node node, int index) {
    if (node.jjtGetNumChildren() > index) {
      Node child = node.jjtGetChild(index);
      if (child != null) {
        return child.value(context);
      }
    }
    return null;
  }

  public static Object getParam(InternalContextAdapter context, Node node, int index,
      Object defaultValue) {
    Object v = getParam(context, node, index);
    if (v == null) {
      return defaultValue;
    }
    return v;
  }
}
