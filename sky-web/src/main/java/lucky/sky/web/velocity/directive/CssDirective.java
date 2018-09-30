package lucky.sky.web.velocity.directive;

import lucky.sky.util.config.ConfigParser;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;

/**
 * 输出附加版本信息的 css 标记
 *
 * 示例: #css("/css/upload.css") => <link href="/css/upload.css?v=20151020180705" rel="stylesheet"/>
 */
public class CssDirective extends Directive {

  @Override
  public String getName() {
    return "css";
  }

  @Override
  public int getType() {
    return LINE;
  }

  @Override
  public boolean render(InternalContextAdapter internalContextAdapter, Writer writer, Node node)
      throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
    String url = DirectiveHelper.getParameter(internalContextAdapter, node, 0);
    //替换特殊字符的信息
    String result = ConfigParser.resolveProperties(url);
    String tag = String
        .format("<link href=\"%s?v=%s\" rel=\"stylesheet\"/>", result, LocalDateTime.now());
    writer.write(tag);
    return true;
  }
}
