package lucky.sky.web.spring;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import lucky.sky.web.security.XSSFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author:chaoqiang.zhou
 * @Date:Create in 下午7:27 2018/4/25
 */

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurerAdapter.class})
@Order(Ordered.HIGHEST_PRECEDENCE + 8)
public class WebMvcAutoConfig extends WebMvcConfigurerAdapter {


  /**
   * 注入filterbean的信息
   */
//  @Bean
//  public Filter xssFilter() {
//    return new XSSFilter();
//  }

}
