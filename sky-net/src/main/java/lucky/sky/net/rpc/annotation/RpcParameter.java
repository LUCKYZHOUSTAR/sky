package lucky.sky.net.rpc.annotation;

import java.lang.annotation.*;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Inherited
@Documented
public @interface RpcParameter {

  /**
   * 方法参数或返回值名称
   */
  String name() default "";

  /**
   * 方法参数或返回值描述
   */
  String description() default "";
}
