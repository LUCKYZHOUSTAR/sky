package lucky.sky.net.rpc.annotation;

import lucky.sky.net.rpc.NamingConvention;

import java.lang.annotation.*;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface RpcService {

  /**
   * 服务名称
   */
  String name() default "";

  /**
   * 服务描述
   */
  String description() default "";

  /**
   * 服务方法命名约定
   */
  NamingConvention convention() default NamingConvention.PASCAL;
}
