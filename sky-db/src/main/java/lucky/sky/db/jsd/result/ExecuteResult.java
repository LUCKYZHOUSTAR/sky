package lucky.sky.db.jsd.result;

import lucky.sky.db.jsd.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 执行 SQL 语句结果 on 15/5/21.
 */
public class ExecuteResult implements AutoCloseable {

  private ConnectionManager manager;
  private String sql;
  private Object[] args;
  private Connection conn;
  private PreparedStatement statement;
  private int affectedRows;
  private ResultSet rs;

  public ExecuteResult(ConnectionManager manager, String sql, Object[] args) {
    this.manager = manager;
    this.sql = sql;
    this.args = args;
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
//        this.prepare();
//        if (rs == null) return null;
//
//        try {
//            return rs.getMetaData();
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
   * 是否有更多的结果集, 如果有则移动到下一个结果集
   *
   * @return 是否有更多结果集
   */
  public boolean more() {
    try {
      boolean hasMore = this.statement.getMoreResults();
      rs = hasMore ? statement.getResultSet() : null;
      return hasMore;
    } catch (Exception e) {
      throw new JsdException(e);
    }
  }

  /**
   * 关闭数据库资源
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
    if (this.statement == null) {
      try {
        conn = this.manager.getConnection();
        statement = conn.prepareStatement(sql);
        if (args != null) {
          for (int i = 0; i < args.length; i++) {
            statement.setObject(i + 1, args[i]);
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

}
