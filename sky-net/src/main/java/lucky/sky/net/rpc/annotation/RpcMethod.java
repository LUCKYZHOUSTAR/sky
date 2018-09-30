package lucky.sky.net.rpc.annotation;

import java.lang.annotation.*;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface RpcMethod {

  /**
   * 方法名称
   */
  String name() default "";

  /**
   * 方法描述
   */
  String description() default "";
}
