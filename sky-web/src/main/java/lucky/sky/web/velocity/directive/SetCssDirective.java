package lucky.sky.web.velocity.directive;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import lucky.sky.util.config.ConfigParser;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * @Author:chaoqiang.zhou
 * @Date:Create in 下午5:46 2018/6/27
 *
 * /** 输出附加版本信息的 css 标记
 *
 * 示例: #setcss("/css/upload.css","/css/down.css") => <link href="/css/upload.css?v=20151020180705"
 * rel="stylesheet"/>
 */
public class SetCssDirective extends Directive {

  @Override
  public String getName() {
    return "setCss";
  }

  @Override
  public int getType() {
    return LINE;
  }

  @Override
  public boolean render(InternalContextAdapter internalContextAdapter, Writer writer, Node node)
      throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      String url = DirectiveHelper.getParameter(internalContextAdapter, node, i);
      //替换特殊字符的信息
      String result = ConfigParser.resolveProperties(url);
      String tag = String
          .format("<link href=\"%s?v=%s\" rel=\"stylesheet\"/>", result, LocalDateTime.now());
      sb.append(tag).append(System.getProperty("line.separator"));
    }
    writer.write(sb.toString());
    return true;
  }
}
