package lucky.sky.util.ioc;

import lucky.sky.util.lang.UncheckedException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 基于 Spring IoC 实现的 ServiceLocator 对象。
 */
public class SpringServiceLocator implements ServiceLocator, ApplicationContextAware {

  private static SpringServiceLocator current;
  private ApplicationContext ctx;

  @Override
  public Object getInstance(String name) {
    return ctx.getBean(name);
  }

  @Override
  public <T> T getInstance(Class<T> serviceType) {
    return ctx.getBean(serviceType);
  }

  @Override
  public <T> T getInstance(String name, Class<T> serviceType) {
    return ctx.getBean(name, serviceType);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    ctx = applicationContext;
    SpringServiceLocator.current = this;
    ServiceLocator.setCurrent(this);
  }

  public ApplicationContext getApplicationContext() {
    return ctx;
  }

  public static void current(SpringServiceLocator l) {
    current = l;
  }

  /**
   * throw if not exist
   */
  public static SpringServiceLocator current() {
    if (current == null) {
      throw new UncheckedException("current SpringServiceLocator is null");
    }
    return current;
  }

  /**
   * return null if not exist
   */
  public static SpringServiceLocator getCurrent() {
    return current;
  }
}
