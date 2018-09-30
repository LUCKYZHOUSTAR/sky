package lucky.sky.util.security.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识需要授权访问。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {

  /**
   * 需要的权限
   */
  String[] value() default {};

  /**
   * 是否任意权限即可，即只要登录即可访问，默认 false
   */
  boolean any() default false;

  /**
   * false 表示多个权限值为 or 的关系，tru 为 and 关系，默认 false
   */
  boolean andOr() default false;
}
