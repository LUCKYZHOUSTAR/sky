package lucky.sky.util.ioc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceLocatorAutoConfig {

  @Autowired
  private ApplicationContext ctx;

//     private static final Logger log = LoggerManager.getLogger(ServiceLocatorAutoConfig.class);

  public ServiceLocatorAutoConfig() {
    // default ctor
  }

  // todo: 其实不需要直接暴露 ServiceLocator 实例, 将来可以找一个更好的实现方法来初始化 ServiceLocator.current
  @Bean(name = "larkServiceLocator")
  @ConditionalOnMissingBean
  public ServiceLocator serviceLocator() {
    // TODO: to fix
    // NOT WORK, org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'serviceLocator': Requested bean is currently in creation: Is there an unresolvable circular reference?
//        try {
//            // TODO workaround, to avoid duplicate, see Application.run
//            if (ctx.containsBean("serviceLocator")) {
//                return (ServiceLocator) ctx.getBean("serviceLocator");
//            }
//        } catch (Exception ex) {
//            log.error("failed to resolve ServiceLocator from Application Context", ex);
//        }
    //
    SpringServiceLocator l = new SpringServiceLocator();
    l.setApplicationContext(ctx);
    return l;
  }
}
