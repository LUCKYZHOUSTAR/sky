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
 * 输出附加版本信息的 js 标记 <p> 示例: #js("/js/upload.js") => <script src="/js/upload.js?v=20151020180705"
 * type="text/javascript"></script>
 */
public class JsDirective extends Directive {

  //getName：指令的名称
  //getType:当前有LINE,BLOCK两个值，line行指令，不要end结束符，block块指令，需要end结束符
  @Override
  public String getName() {
    return "js";
  }

  @Override
  public int getType() {
    return LINE;
  }

  //加上时间戳操作
  @Override
  public boolean render(InternalContextAdapter internalContextAdapter, Writer writer, Node node)
      throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
    //node.jjtGetChild(2) 这个方法可以获取对应指令的参数，下标从0开始
    String url = DirectiveHelper.getParameter(internalContextAdapter, node, 0);
    //替换${propertyname}占位符信息
    String result = ConfigParser.resolveProperties(url);
    String tag = String.format("<script src=\"%s?v=%s\" type=\"text/javascript\"></script>", result,
        LocalDateTime.now());
    writer.write(tag);
    return true;
  }
}
