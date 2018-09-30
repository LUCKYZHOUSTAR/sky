package lucky.sky.db.jsd.clause;


public interface ColumnsClause {

  /**
   * 设置要插入记录的数据值
   *
   * @param values 数据列值
   */
  ValuesClause values(Object... values);
}
