package lucky.sky.util.lang;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 提供对类型、成员的描述性信息。
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {

  String value() default "";
}
