package lucky.sky.task;

import java.lang.annotation.*;

/**
 *
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Task {

  String name() default "";

}
