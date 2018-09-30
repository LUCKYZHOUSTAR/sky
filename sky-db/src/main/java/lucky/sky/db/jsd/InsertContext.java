package lucky.sky.db.jsd;

import lucky.sky.db.jsd.clause.ColumnsClause;
import lucky.sky.db.jsd.clause.InsertClause;
import lucky.sky.db.jsd.clause.InsertEndClause;
import lucky.sky.db.jsd.clause.ValuesClause;
import lucky.sky.db.jsd.result.BuildResult;
import lucky.sky.db.jsd.result.InsertResult;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 插入操作上下文 on 15/5/11.
 */
public class InsertContext implements InsertClause, ColumnsClause, ValuesClause, InsertEndClause {

  @Getter
  private InsertInfo info;
  private ConnectionManager manager;
  private Builder builder;

  InsertContext(ConnectionManager manager, Builder builder, String table) {
    this.manager = manager;
    this.builder = builder;
    this.info = new InsertInfo(table);
  }

  InsertContext(ConnectionManager manager, Builder builder, Object obj) {
    this(manager, builder, obj, null);
  }

  InsertContext(ConnectionManager manager, Builder builder, Object obj, String table) {
    if (obj == null) {
      throw new JsdException("parameter obj can't be null or empty");
    }

    this.manager = manager;
    this.builder = builder;

    Mapper.EntityInfo entityInfo = Mapper.getEntityInfo(obj.getClass());
    this.info = new InsertInfo(table == null ? entityInfo.table : table);
    this.columns(entityInfo.getInsertColumns());
    this.values(entityInfo.getInsertValues(obj));
  }

  <T> InsertContext(ConnectionManager manager, Builder builder, List<T> objects) {
    this(manager, builder, objects, null);
  }

  <T> InsertContext(ConnectionManager manager, Builder builder, List<T> objects, String table) {
    if (objects == null || objects.isEmpty()) {
      throw new JsdException("parameter objects can't be null or empty");
    }

    this.manager = manager;
    this.builder = builder;

    Mapper.EntityInfo entityInfo = Mapper.getEntityInfo(objects.get(0).getClass());
    this.info = new InsertInfo(table == null ? entityInfo.table : table);
    this.columns(entityInfo.getInsertColumns());
    objects.forEach(v -> this.values(entityInfo.getInsertValues(v)));
  }

  @Override
  public BuildResult print() {
    BuildResult result = this.builder.buildInsert(this.info);
    return result;
  }

  @Override
  public InsertResult result() {
    return this.result(false);
  }

  @Override
  public InsertResult result(boolean returnKeys) {
    BuildResult result = this.builder.buildInsert(this.info);
    Debug.log(result);
    Connection conn = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    try {
      conn = this.manager.getConnection();

      int option = returnKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS;
      statement = conn.prepareStatement(result.getSql(), option);
      List<Object> args = result.getArgs();
      if (args != null) {
        for (int i = 0; i < args.size(); i++) {
          statement.setObject(i + 1, args.get(i));
        }
      }

      int rows = statement.executeUpdate();
      List<Object> keys = null;
      if (rows > 0 && returnKeys) {
        keys = new ArrayList<>(rows);
        rs = statement.getGeneratedKeys();
        while (rs.next()) {
          keys.add(rs.getObject(1));
        }
      }

      return new InsertResult(rows, keys);
    } catch (Exception e) {
      throw new JsdException(e);
    } finally {
      Releaser.release(rs);
      Releaser.release(statement);
      Releaser.release(manager, conn);
    }
  }

  @Override
  public ValuesClause values(Object... values) {
    this.info.getValues().add(values);
    return this;
  }

  @Override
  public ColumnsClause columns(String... columns) {
    this.info.setColumns(columns);
    return this;
  }

  @Getter
  static class InsertInfo {

    private String table;
    @Setter
    private String[] columns;
    private List<Object[]> values = new ArrayList<>();

    public InsertInfo(String table) {
      this.table = table;
    }
  }

  // example
//    @javax.persistence.Table(name = "abc")
//    @Entity
//    public static class ABC {
//        @Id                     // 自增主键
//        @javax.persistence.Column(name = "Id")    // 列
//        private int id;
//
//        @Transient              // 表示此数据不在数据库表里建立属性
//        private String temp;
//    }
}
