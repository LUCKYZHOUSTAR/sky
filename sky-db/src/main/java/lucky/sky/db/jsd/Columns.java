package lucky.sky.db.jsd;

import java.util.ArrayList;
import java.util.List;

/**
 * on 15/5/28.
 */
public final class Columns {

  boolean distinct;
  List<Column> list = new ArrayList<>();

  public Columns() {
    // default ctor
  }

  public Columns(Table t, String... cols) {
    this.add(t, cols);
  }

  public Columns distinct(boolean enabled) {
    this.distinct = enabled;
    return this;
  }

  /**
   * add table columns
   */
  public Columns add(Table t, String... cols) {
    for (String col : cols) {
      list.add(new SimpleColumn(t, col, null));
    }
    return this;
  }

  /**
   * add table columns
   */
  public Columns add(Table t, String col) {
    list.add(new SimpleColumn(t, col, null));
    return this;
  }

  /**
   * add table column with alias
   */
  public Columns addWithAlias(Table t, String col, String alias) {
    list.add(new SimpleColumn(t, col, alias));
    return this;
  }

  /**
   * add expression column, like 'COUNT(*)'
   */
  public Columns add(String expr, String alias) {
    list.add(new ExprColumn(expr, alias));
    return this;
  }

  /**
   * 列
   */
  interface Column {

    String getAlias();
  }

  /**
   * 常规数据列
   */
  static class SimpleColumn implements Column {

    Table table;
    String column;
    String alias;

    SimpleColumn(Table table, String column, String alias) {
      this.table = table;
      this.column = column;
      this.alias = alias;
    }

    @Override
    public String getAlias() {
      return alias;
    }
  }

  /**
   * 聚合列
   */
  static class PolyColumn implements Column {

    Table table;
    String column;
    String function;
    String alias;

    @Override
    public String getAlias() {
      return alias;
    }
  }

  /**
   * 表达式列
   */
  static class ExprColumn implements Column {

    String expr;
    String alias;

    ExprColumn(String expr, String alias) {
      this.expr = expr;
      this.alias = alias;
    }

    @Override
    public String getAlias() {
      return alias;
    }
  }
}
