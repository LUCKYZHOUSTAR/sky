package lucky.sky.db.jsd;


public final class Shortcut {

  private Shortcut() {
  }

  public static BasicFilter f() {
    return Filter.create();
  }

  public static BasicFilter f(String column, Object value) {
    return Filter.create(column, value);
  }

  public static BasicFilter f(String column, FilterType filterType, Object value) {
    return Filter.create(column, filterType, value);
  }

  public static BasicFilter f(Table table, String column, FilterType filterType, Object value) {
    return Filter.create().add(table, column, filterType, value);
  }

  public static BasicFilter f(Table table1, String column1, Table table2, String column2) {
    return Filter.create().add(table1, column1, FilterType.EQ, table2, column2);
  }

  public static Table t(String name) {
    return Table.create(name);
  }

  public static Table t(Class<?> clazz) {
    Mapper.EntityInfo info = Mapper.getEntityInfo(clazz);
    return Table.create(info.table);
  }

  public static Table t(String name, String alias) {
    return Table.create(name, alias);
  }

  public static UpdateValues uv() {
    return new UpdateValues();
  }

  public static UpdateValues uv(String column, Object value) {
    return new UpdateValues(column, value);
  }

  public static UpdateValues uv(String column, UpdateType type, Object value) {
    return new UpdateValues().add(column, type, value);
  }

  /**
   * 分组子句快捷生成方法
   *
   * @param columns 列名列表
   */
  public static Groupers g(String... columns) {
    return new Groupers(columns);
  }

  /**
   * 分组子句快捷生成方法
   *
   * @param table 表
   * @param columns 列名列表
   */
  public static Groupers g(Table table, String... columns) {
    return new Groupers(table, columns);
  }

  /**
   * 排序子句快捷生成方法
   *
   * @param sortType 排序方式
   * @param columns 列名列表
   */
  public static Sorters s(SortType sortType, String... columns) {
    return new Sorters(sortType, columns);
  }

  /**
   * 排序子句快捷生成方法
   *
   * @param sortType 排序方式
   * @param table 表
   * @param columns 列名列表
   */
  public static Sorters s(SortType sortType, Table table, String... columns) {
    return new Sorters(sortType, table, columns);
  }

  /**
   * 查询列快捷生成方法
   */
  public static Columns c() {
    return new Columns();
  }

  /**
   * 查询列快捷生成方法
   *
   * @param expr 表达式, 如: COUNT(*)
   * @param alias 别名, 如: Count
   */
  public static Columns c(String expr, String alias) {
    return new Columns().add(expr, alias);
  }

  /**
   * 存储过程参数快捷生成方法
   */
  public static CallParams p(int paramCount) {
    return new CallParams(paramCount);
  }

  /**
   * COUNT(0) AS count 查询列快捷生成方法, 等价于 c("COUNT(0)", "count")
   */
  public static Columns count() {
    return new Columns().add("COUNT(0)", "count");
  }

  /**
   * 查询列快捷生成方法
   *
   * @param table 表
   * @param columns 列名
   */
  public static Columns c(Table table, String... columns) {
    return new Columns(table, columns);
  }

  /**
   * 查询列快捷生成方法
   *
   * @param table 表
   * @param columns 列名列表, 逗号分割
   */
  public static Columns cs(Table table, String columns) {
    return new Columns(table, columns.split(","));
  }

  /**
   * 查询列快捷生成方法
   *
   * @param table 表名
   * @param columns 列名列表, 逗号分割
   */
  public static Columns cs(String table, String columns) {
    return new Columns(t(table), columns.split(","));
  }

  /**
   * 查询列快捷生成方法
   *
   * @param columns 列名列表, 逗号分割
   */
  public static Columns cs(String columns) {
    return new Columns(null, columns.split(","));
  }
}
