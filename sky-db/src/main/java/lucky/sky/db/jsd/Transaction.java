package lucky.sky.db.jsd;

/**
 * on 15/5/21.
 */
public interface Transaction extends Query, AutoCloseable {

  /**
   * 获取当前上下文中的事务对象
   */
  static Transaction get() {
    return DatabaseFactory.TransactionImp.current.get();
  }

  /**
   * 回滚事务
   */
  void rollback();

  /**
   * 提交事务
   */
  void commit();

  /**
   * 关闭事务, 如果当前有未关闭的事务则自动回滚
   */
  @Override
  void close();
}
