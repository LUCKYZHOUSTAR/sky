package lucky.sky.web.velocity.directive;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.Node;


public final class DirectiveHelper {

  @SuppressWarnings("unchecked")
  public static <T> T getParameter(InternalContextAdapter context, Node node, int paramIndex) {
    if (node.jjtGetChild(paramIndex) != null) {
      Node n = node.jjtGetChild(paramIndex);
      return n == null ? null : (T) n.value(context);
    }
    return null;
  }
}
