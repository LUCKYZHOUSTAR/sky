package lucky.sky.db.jsd;

import lucky.sky.db.jsd.clause.CallClause;
import lucky.sky.db.jsd.clause.CallEndClause;
import lucky.sky.db.jsd.result.BuildResult;
import lucky.sky.db.jsd.result.CallResult;

import java.util.Arrays;

/**
 * 存储过程执行上下文 on 15/5/11.
 */
public final class CallContext implements CallClause {

  private ConnectionManager manager;
  private CallInfo info;

  CallContext(ConnectionManager manager, String sp) {
    this.manager = manager;
    this.info = new CallInfo(sp);
  }

  @Override
  public CallResult result() {
    BuildResult result = this.buildCall();
    Debug.log(result);
    return new CallResult(manager, result.getSql(), this.info.params);
  }

  @Override
  public BuildResult print() {
    return this.buildCall();
  }

  @Override
  public CallEndClause with(CallParams params) {
    this.info.params = params;
    return this;
  }

  private BuildResult buildCall() {
    BuildBuffer buffer = new BuildBuffer();

    buffer.addSql("{");
    if (this.info.params != null && this.info.params.hasReturnParam()) {
      buffer.addSql("? = ");
    }
    buffer.addSql("call %s(", this.info.sp);
    if (this.info.params != null && this.info.params.paramCount > 0) {
      String[] array = new String[this.info.params.paramCount];
      Arrays.fill(array, "default");
      for (CallParams.CallParam param : this.info.params.getParams()) {
        array[param.getIndex() - 1] = "?";
      }
      buffer.addSql(String.join(",", array));
    }
    buffer.addSql(")}");
    return buffer.getResult();
  }

  static class CallInfo {

    private String sp;
    private CallParams params;

    CallInfo(String sp) {
      this.sp = sp;
    }
  }
}
