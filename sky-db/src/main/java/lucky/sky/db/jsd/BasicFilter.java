package lucky.sky.db.jsd;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 */
public final class BasicFilter extends Filter {

  private List<FilterItem> items;

  BasicFilter() {
  }

  public List<FilterItem> getItems() {
    return items;
  }

  boolean hasItems() {
    return items != null && !items.isEmpty();
  }

  /**
   * 添加自定义表达式查询条件(慎用, 使用此方法将不能保证跨数据库兼容)
   *
   * @param expr 表达式, 如: SUM(COUNT)>100
   */
  public BasicFilter add(String expr) {
    this.add(new ExprFilterItem(expr));
    return this;
  }

  /**
   * 添加简单查询条件
   *
   * @param column 列
   * @param value 值
   */
  public BasicFilter add(String column, Object value) {
    this.add(new OneColumnFilterItem(column, value));
    return this;
  }

  /**
   * 添加指定类型的查询条件
   *
   * @param column 列
   * @param filterType 类型
   * @param value 值
   */
  public BasicFilter add(String column, FilterType filterType, Object value) {
    this.add(new OneColumnFilterItem(null, column, filterType, value));
    return this;
  }

  public BasicFilter add(Table table, String column, FilterType filterType, Object value) {
    this.add(new OneColumnFilterItem(table, column, filterType, value));
    return this;
  }

  public BasicFilter add(Table table1, String column1, Table table2, String column2) {
    this.add(new TwoColumnFilterItem(table1, column1, FilterType.EQ, table2, column2));
    return this;
  }

  public BasicFilter add(Table table1, String column1, FilterType filterType, Table table2,
      String column2) {
    this.add(new TwoColumnFilterItem(table1, column1, filterType, table2, column2));
    return this;
  }

  private void add(FilterItem item) {
    if (this.items == null) {
      items = new ArrayList<>();
    }
    items.add(item);
  }
//        IfAdd(when bool, col string, value interface{}) BasicFilters
//        IfAddT(when bool, col string, ft filterType, value interface{}) BasicFilters
//        IfAddF(when bool, t Table, col string, ft filterType, value interface{}) BasicFilters
//        IfAddJ(when bool, t1 Table, col1 string, ft filterType, t2 Table, col2 string) BasicFilters
//        IfAddE(when bool, expr string) BasicFilters

  /**
   * 条件项类型
   */
  enum FilterItemType {
    EXPR, ONE_COLUMN, TWO_COLUMN;
  }

  interface FilterItem {

    FilterItemType getItemType();
  }

  @Getter
  static class OneColumnFilterItem implements FilterItem {

    private Table table;
    private FilterType type;
    private String column;
    private Object value;

    OneColumnFilterItem(String col, Object val) {
      this.column = col;
      this.type = FilterType.EQ;
      this.value = val;
    }

    OneColumnFilterItem(Table t, String col, FilterType type, Object val) {
      this.table = t;
      this.column = col;
      this.type = type;
      this.value = val;
    }

    @Override
    public FilterItemType getItemType() {
      return FilterItemType.ONE_COLUMN;
    }
  }

  @Getter
  static class TwoColumnFilterItem implements FilterItem {

    private Table table1;
    private String column1;
    private FilterType type;
    private Table table2;
    private String column2;

    TwoColumnFilterItem(Table t1, String col1, FilterType ft, Table t2, String col2) {
      this.table1 = t1;
      this.column1 = col1;
      this.type = ft;
      this.table2 = t2;
      this.column2 = col2;
    }

    @Override
    public FilterItemType getItemType() {
      return FilterItemType.TWO_COLUMN;
    }
  }

  @Getter
  static class ExprFilterItem implements FilterItem {

    private String expr;

    ExprFilterItem(String expr) {
      this.expr = expr;
    }

    @Override
    public FilterItemType getItemType() {
      return FilterItemType.EXPR;
    }
  }
}
