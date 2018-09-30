package lucky.sky.db.jsd;

/**
 * 事务取消异常, 在 Database 的 begin 方法中, 匿名方法如果抛出这个异常, 事务会 rollback, 但不会把此异常再往外抛
 */
public class TxCancelledException extends JsdException {

  public static final TxCancelledException INSTANCE = new TxCancelledException();

  public TxCancelledException() {
    super();
  }
}
