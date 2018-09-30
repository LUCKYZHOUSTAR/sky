package lucky.sky.db.jsd.result;

import java.util.List;

/**
 * on 15/5/11.
 */
public class InsertResult extends SimpleResult {

  private List<Object> keys;

  public InsertResult(int affectedRows, List<Object> keys) {
    super(affectedRows);
    this.keys = keys;
  }

  /**
   * 获取自动生成的自增主键列表
   */
  public List<Object> getKeys() {
    return this.keys;
  }
}
