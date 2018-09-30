package lucky.sky.db.jsd.result;

import lucky.sky.db.jsd.*;
import lucky.sky.db.jsd.converter.ConverterManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * on 15/5/27.
 */
public final class SelectResult implements AutoCloseable {

  private ConnectionManager manager;
  private String sql;
  private List<Object> args;
  private ResultSet rs;
  private Connection conn;
  private PreparedStatement statement;

  public SelectResult(ConnectionManager manager, String sql, List<Object> args) {
    this.manager = manager;
    this.sql = sql;
    this.args = args;
  }

  /**
   * 读取第一行记录的第一列, 并在读取后关闭数据库资源
   */
  @SuppressWarnings("unchecked")
  public <T> T value(Class<T> clazz) {
    return (T) ConverterManager.d2j(clazz, value());
  }

  /**
   * 读取第一行记录的第一列, 并在读取后关闭数据库资源
   */
  public Object value() {
    try {
      ResultSet rs = getResultSet();
      if (rs.next()) {
        return rs.getObject(1);
      }
      return null;
    } catch (Exception e) {
      throw new JsdException(e);
    } finally {
      this.close();
    }
  }

  /**
   * 读取第一行记录, 并在读取后关闭数据库资源
   */
  public Map<String, Object> one() {
    try {
      ResultSet rs = getResultSet();
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
    } finally {
      this.close();
    }
  }

  /**
   * 读取一行记录转换为指定的数据实体, 并在读取后关闭数据库资源
   */
  public <T> T one(Class<T> clazz) {
    try {
      ResultSet rs = getResultSet();
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
    } finally {
      this.close();
    }
  }

  /**
   * 读取所有记录转换为指定的数据实体, 并在读取所有数据后关闭数据库资源
   */
  public <T> List<T> all(Class<T> clazz) {
    try {
      List<T> list = new ArrayList<>();
      ResultSet rs = getResultSet();
      ResultSetMetaData metaData = rs.getMetaData();
      Mapper.EntityInfo info = Mapper.getEntityInfo(clazz);
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
    } finally {
      this.close();
    }
  }

  /**
   * 订阅处理每一条数据, 并在读取所有数据后关闭数据库资源
   */
  public void each(Consumer<DataReader> action) {
    try {
      ResultSet rs = getResultSet();
      DataReader reader = DataReader.create(rs);
      while (rs.next()) {
        action.accept(reader);
      }
    } catch (Exception e) {
      throw new JsdException(e);
    } finally {
      this.close();
    }
  }

  /**
   * 移动到下一行数据记录
   */
//    public boolean read() {
//        return false;
//    }
  @Override
  public void close() {
    Releaser.release(rs);
    Releaser.release(statement);
    Releaser.release(manager, conn);
    rs = null;
    statement = null;
    conn = null;
  }

  private ResultSet getResultSet() throws SQLException {
    if (rs != null) {
      return rs;
    }

    conn = this.manager.getConnection();
    statement = conn.prepareStatement(sql);
    if (args != null) {
      for (int i = 0; i < args.size(); i++) {
        statement.setObject(i + 1, args.get(i));
      }
    }
    rs = statement.executeQuery();
    return rs;
  }
}
