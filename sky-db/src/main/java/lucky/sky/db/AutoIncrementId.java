package lucky.sky.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示基于数据源实现的自增长ID
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AutoIncrementId {
  // TODO initial & step, by default, initial/step = 1
}
