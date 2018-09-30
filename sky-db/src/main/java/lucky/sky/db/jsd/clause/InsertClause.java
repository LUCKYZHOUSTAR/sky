package lucky.sky.db.jsd.clause;

/**
 * on 15/5/8.
 */
public interface InsertClause {

  /**
   * 设置要插入记录的数据列
   *
   * @param columns 数据列
   */
  ColumnsClause columns(String... columns);

  /**
   * 设置要插入记录的数据值
   *
   * @param values 数据列值
   */
  ValuesClause values(Object... values);
}

