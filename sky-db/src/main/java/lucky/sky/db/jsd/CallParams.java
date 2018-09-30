package lucky.sky.db.jsd;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * on 15/5/27.
 */
public final class CallParams {

  int paramCount;
  private CallParam returnParam;
  private List<CallParam> params = new ArrayList<>();

  public CallParams(int paramCount) {
    this.paramCount = paramCount;
  }

  public CallParams add(int index, Object value) {
    CallParam param = new CallParam(index, value);
    params.add(param);
    return this;
  }

  public CallParams addOut(int index, JDBCType sqlType) {
    CallParam param = new CallParam(CallParamType.OUT, index, sqlType);
    params.add(param);
    return this;
  }

  public CallParams addInOut(int index, JDBCType sqlType, Object value) {
    CallParam param = new CallParam(CallParamType.INOUT, index, sqlType, value);
    params.add(param);
    return this;
  }

  public CallParams addReturn(JDBCType sqlType) {
    returnParam = new CallParam(CallParamType.RETURN, 1, sqlType);
    params.add(returnParam);
    return this;
  }

  public List<CallParam> getParams() {
    return params;
  }

  public boolean hasReturnParam() {
    return returnParam != null;
  }

  /**
   * 参数信息
   */
  public static class CallParam {

    CallParamType paramType;
    int index;
    JDBCType sqlType;
    Object value;

    CallParam(int index, Object value) {
      this.paramType = CallParamType.IN;
      this.index = index;
      this.value = value;
    }

    CallParam(CallParamType paramType, int index, JDBCType sqlType) {
      this.paramType = paramType;
      this.index = index;
      this.sqlType = sqlType;
    }

    CallParam(CallParamType paramType, int index, JDBCType sqlType, Object value) {
      this.paramType = paramType;
      this.index = index;
      this.sqlType = sqlType;
      this.value = value;
    }

    public CallParamType getParamType() {
      return paramType;
    }

    public int getIndex() {
      return index;
    }

    public JDBCType getSqlType() {
      return sqlType;
    }

    public Object getValue() {
      return value;
    }
  }

  /**
   * 参数类型
   */
  public enum CallParamType {
    /**
     * 输入参数
     */
    IN,
    /**
     * 输出参数
     */
    OUT,
    /**
     * 参数既能输入，也能输出
     */
    INOUT,
    /**
     * 参数表示诸如存储过程、内置函数或用户定义函数之类的操作的返回值
     */
    RETURN,
  }
}
