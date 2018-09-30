package lucky.sky.db.jsd;

import lombok.Getter;

/**
 * 过滤条件
 *
 * on 15/5/13.
 */
public abstract class Filter {

  /**
   * 返回一个对立的条件
   */
  public Filter not() {
    return new NotFilter(this);
  }

  /**
   * 返回一个与关系的条件
   */
  public Filter and(Filter filter) {
    return new AndFilter(this, filter);
  }

  /**
   * 返回一个或关系的条件
   */
  public Filter or(Filter filter) {
    return new OrFilter(this, filter);
  }

  /**
   * 创建一个基础过滤条件
   */
  public static BasicFilter create() {
    return new BasicFilter();
  }

  /**
   * 创建一个基础过滤条件
   */
  public static BasicFilter create(String column, Object value) {
    return new BasicFilter().add(column, value);
  }

  /**
   * 创建一个基础过滤条件
   */
  public static BasicFilter create(String column, FilterType filterType, Object value) {
    return new BasicFilter().add(column, filterType, value);
  }

  static class AndFilter extends Filter {

    @Getter
    private Filter left;
    @Getter
    private Filter right;

    AndFilter(Filter left, Filter right) {
      this.left = left;
      this.right = right;
    }
  }

  static class OrFilter extends Filter {

    @Getter
    private Filter left;
    @Getter
    private Filter right;

    OrFilter(Filter left, Filter right) {
      this.left = left;
      this.right = right;
    }
  }

  static class NotFilter extends Filter {

    @Getter
    private Filter inner;

    NotFilter(Filter filter) {
      this.inner = filter;
    }
  }
}
