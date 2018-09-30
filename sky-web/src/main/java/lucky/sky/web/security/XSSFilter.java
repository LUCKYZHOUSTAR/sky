package lucky.sky.web.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import lucky.sky.util.config.ConfigProperties;
import lucky.sky.web.HttpRequestValidationException;
import org.springframework.web.util.HtmlUtils;

/**
 * @Author:chaoqiang.zhou
 * @Date:Create in 下午7:02 2018/4/25
 */
public class XSSFilter implements Filter {


  private FilterConfig filterConfig;
  //默认是转义字符，后期如果要加入clean策略的话，可以采用http://www.freebuf.com/sectool/134015.html，但是性能会受到影响
  private XSSFilterMode mode = XSSFilterMode
      .valueOf(ConfigProperties.getProperty("sky.web.xss.filter.mode", "ESCAPE").toUpperCase());


  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

    this.filterConfig = filterConfig;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {

    if (mode == XSSFilterMode.NONE) {
      filterChain.doFilter(servletRequest, servletResponse);
    } else {
      filterChain
          .doFilter(new RequestWrapper((HttpServletRequest) servletRequest), servletResponse);

    }
  }

  @Override
  public void destroy() {

    this.filterConfig = null;
  }


  private final class RequestWrapper extends HttpServletRequestWrapper {


    RequestWrapper(HttpServletRequest servletRequest) {
      super(servletRequest);
    }

    @Override
    public String[] getParameterValues(String parameter) {
      String[] values = super.getParameterValues(parameter);
      if (values == null) {
        return null;
      }
      int count = values.length;
      String[] encodedValues = new String[count];
      for (int i = 0; i < count; i++) {
        encodedValues[i] = filterXSS(values[i]);
      }
      return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
      String value = super.getParameter(parameter);
      if (value == null) {
        return null;
      }
      return filterXSS(value);
    }

    @Override
    public String getHeader(String name) {
      String value = super.getHeader(name);
      if (value == null) {
        return null;
      }
      return filterXSS(value);
    }
  }


  private String filterXSS(String value) {
    switch (mode) {
      case ESCAPE:
        return HtmlUtils.htmlEscape(value);
      case REJECT:
        validateXSS(value);
        return value;
      default:
        throw new IllegalStateException("not supported mode: " + mode);
    }
  }

  private void validateXSS(String value) {
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      if (ch == '<' || ch == '>') {
        throw new HttpRequestValidationException();
      }
    }
  }
}
