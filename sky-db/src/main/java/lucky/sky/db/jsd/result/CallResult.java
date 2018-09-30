package lucky.sky.db.jsd.result;

import lucky.sky.db.jsd.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 存储过程执行结果 on 15/5/11.
 */
@SuppressWarnings("unchecked")
public class CallResult implements AutoCloseable {

  private ConnectionManager manager;
  private Connection conn;
  private CallableStatement statement;
  private ResultSet rs;
  private String sql;
  private CallParams params;
  private int affectedRows;

  public CallResult(ConnectionManager manager, String sql, CallParams params) {
    this.manager = manager;
    this.sql = sql;
    this.params = params;
  }

  /**
   * 获取操作受影响的行数
   */
  public int getAffectedRows() {
    this.prepare();
    return this.affectedRows;
  }

  /**
   * 获取当前结果集的元数据
   *
   * @return
   */
//    public ResultSetMetaData getMetaData() {
//        try {
//            return this.resultSet.getMetaData();
//        } catch (SQLException e) {
//            throw new JsdException(e);
//        }
//    }

  /**
   * 读取一行记录
   */
  public Map<String, Object> one() {
    prepare();
    if (rs == null) {
      return null;
    }

    try {
      if (rs.next()) {
        Map<String, Object> map = new HashMap<>();
        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
          map.put(metaData.getColumnLabel(i), rs.getObject(i));
        }
        return map;
      }
      return null;
    } catch (Exception e) {
      throw new JsdException(e);
    }
  }

  /**
   * 读取一行记录转换为指定的数据实体
   */
  public <T> T one(Class<T> clazz) {
    prepare();
    if (rs == null) {
      return null;
    }

    try {
      if (rs.next()) {
        Mapper.EntityInfo info = Mapper.getEntityInfo(clazz);
        T obj = clazz.newInstance();
        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
          Object value = rs.getObject(i);
          if (value != null) {
            info.setValue(obj, metaData.getColumnLabel(i), value);
          }
        }
        return obj;
      }
      return null;
    } catch (Exception e) {
      throw new JsdException(e);
    }
  }

  /**
   * 读取当前集合所有记录并转换为指定的数据实体
   */
  public <T> List<T> all(Class<T> clazz) {
    prepare();
    if (rs == null) {
      return null;
    }

    try {
      List<T> list = new ArrayList<>();
      Mapper.EntityInfo info = Mapper.getEntityInfo(clazz);
      ResultSetMetaData metaData = rs.getMetaData();
      while (rs.next()) {
        T obj = clazz.newInstance();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
          info.setValue(obj, metaData.getColumnLabel(i), rs.getObject(i));
        }
        list.add(obj);
      }
      return list;
    } catch (Exception e) {
      throw new JsdException(e);
    }
  }

  /**
   * 订阅处理当前集合的每一条数据
   */
  public void each(Consumer<DataReader> action) {
    prepare();
    if (rs == null) {
      return;
    }

    try {
      DataReader reader = DataReader.create(rs);
      while (rs.next()) {
        action.accept(reader);
      }
    } catch (Exception e) {
      throw new JsdException(e);
    }
  }

  /**
   * 移动到下一个结果集
   */
  public boolean more() {
    try {
      boolean ok = statement.getMoreResults();
      if (ok) {
        rs = statement.getResultSet();
      }
      return ok;
    } catch (SQLException e) {
      throw new JsdException(e);
    }
  }

  /**
   * 移动到下一个数据行
   *
   * @return
   */
//    public boolean read() {
//        try {
//            if (resultSet == null) {
//                resultSet = statement.getResultSet();
//            }
//            return resultSet.next();
//        } catch (SQLException e) {
//            throw new JsdException(e);
//        }
//    }

  /**
   * 获取返回参数值
   *
   * @param index 从1开始的列序号
   * @param type 参数类型
   */
  public <T> T getParam(int index, Class<T> type) {
    prepare();

    try {
      return (T) statement.getObject(params.hasReturnParam() ? index + 1 : index);
    } catch (SQLException e) {
      throw new JsdException(e);
    }
  }

  /**
   * 获取返回参数值
   *
   * @param name 参数名称
   * @param type 参数类型
   */
  public <T> T getParam(String name, Class<T> type) {
    prepare();

    try {
      return (T) statement.getObject(name);
    } catch (SQLException e) {
      throw new JsdException(e);
    }
  }

  /**
   * 获取返回值(必须先启用返回值)
   *
   * @param type 返回值类型
   */
  public <T> T getReturn(Class<T> type) {
    prepare();

    if (!this.params.hasReturnParam()) {
      throw new JsdException("return parameter isn't registered");
    }

    try {
      return (T) statement.getObject(1);
    } catch (SQLException e) {
      throw new JsdException(e);
    }
  }

  /**
   * 释放资源
   */
  @Override
  public void close() {
    Releaser.release(rs);
    Releaser.release(statement);
    Releaser.release(manager, conn);
    rs = null;
    statement = null;
    conn = null;
  }

  private void prepare() {
    if (this.statement != null) {
      return;
    }

    try {
      conn = this.manager.getConnection();
      statement = conn.prepareCall(this.sql);
      if (params != null) {
        for (CallParams.CallParam param : params.getParams()) {
          switch (param.getParamType()) {
            case IN:
              statement.setObject(params.hasReturnParam() ? param.getIndex() + 1 : param.getIndex(),
                  param.getValue());
              break;
            case OUT:
              statement.registerOutParameter(
                  params.hasReturnParam() ? param.getIndex() + 1 : param.getIndex(),
                  param.getSqlType().getVendorTypeNumber());
              break;
            case RETURN:
              statement.registerOutParameter(1, param.getSqlType().getVendorTypeNumber());
              break;
            case INOUT:
              statement.setObject(params.hasReturnParam() ? param.getIndex() + 1 : param.getIndex(),
                  param.getValue());
              statement.registerOutParameter(
                  params.hasReturnParam() ? param.getIndex() + 1 : param.getIndex(),
                  param.getSqlType().getVendorTypeNumber());
              break;
          }
        }
      }

      boolean isResultSet = statement.execute();
      if (isResultSet) {
        rs = statement.getResultSet();
      } else {
        affectedRows = statement.getUpdateCount();
      }
    } catch (SQLException e) {
      throw new JsdException(e);
    }
  }
}
