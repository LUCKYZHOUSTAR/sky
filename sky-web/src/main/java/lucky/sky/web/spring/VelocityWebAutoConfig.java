//package lucky.sky.web.spring;
//
//import java.util.HashMap;
//import java.util.Map;
//import javax.servlet.Servlet;
//import org.apache.velocity.app.VelocityEngine;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
//import org.springframework.boot.autoconfigure.velocity.VelocityProperties;
//import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.PriorityOrdered;
//import org.springframework.core.annotation.Order;
//import org.springframework.ui.velocity.VelocityEngineFactory;
//import org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver;
//import org.springframework.web.servlet.view.velocity.VelocityViewResolver;
//
//@Configuration
//@ConditionalOnWebApplication
//@ConditionalOnClass({VelocityEngine.class, VelocityEngineFactory.class, Servlet.class})
//@AutoConfigureAfter(WebMvcAutoConfiguration.class)
//@Order(Ordered.HIGHEST_PRECEDENCE + 8)
//public class VelocityWebAutoConfig {
//
//  @Autowired
//  protected VelocityProperties properties;
//
//  @Bean
//  @Order(PriorityOrdered.HIGHEST_PRECEDENCE)
//  @ConditionalOnMissingBean(name = {"velocityViewResolver"})
//  @ConditionalOnProperty(name = {"spring.velocity.enabled"}, matchIfMissing = true)
//  public VelocityViewResolver velocityViewResolver() {
//    VelocityLayoutViewResolver resolver = new VelocityLayoutViewResolver();
//    Map parameter = new HashMap();
//    parameter.put("staticSiteUrl", "test");
//    properties.setProperties(parameter);
//    this.properties.applyToViewResolver(resolver);
//    return resolver;
//  }
//
//
//}
//
