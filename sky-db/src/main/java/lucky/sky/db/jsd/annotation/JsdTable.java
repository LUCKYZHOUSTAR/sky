package lucky.sky.db.jsd.annotation;

import lucky.sky.db.jsd.NameStyle;

import java.lang.annotation.*;


@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsdTable {

  /**
   * 表字段命名风格
   */
  NameStyle nameStyle() default NameStyle.PASCAL;

  /**
   * 表分片字段
   */
  String[] shardKeys() default {};
}
