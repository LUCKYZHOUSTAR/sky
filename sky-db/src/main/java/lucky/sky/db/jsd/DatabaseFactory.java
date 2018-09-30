package lucky.sky.db.jsd;

import lucky.sky.db.config.DatabaseConfig;
import lucky.sky.db.config.ShardConfig;
import lucky.sky.db.jdbc.DataSourceFactory;
import lucky.sky.db.jsd.clause.*;
import lucky.sky.db.jsd.result.BuildResult;
import lucky.sky.db.jsd.template.SqlTemplate;
import lucky.sky.util.config.ConfigException;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * on 15/6/4.
 */
public final class DatabaseFactory {

  private final static HashMap<String, Database> databases = new HashMap<>();

  /**
   * 打开数据库
   *
   * @param name 数据库名称(在 db.sql.conf 中配置)
   */
  public synchronized static Database open(String name) {
    Database db = databases.get(name);
    if (db != null) {
      return db;
    }

    DataSource source = DataSourceFactory.get(name);
    DatabaseConfig.SqlDBInfo di = DatabaseConfig.get(name);
    db = new DatabaseImp(source, Builder.get(di.getType()));
    databases.put(name, db);
    return db;
  }

  /**
   * 打开分片数据库读节点
   *
   * @param name 数据库名称(在 db.sql.conf 中配置)
   * @param keys 分片参数
   */
  public static Database openReadShard(String name, Object... keys) {
    ShardConfig.Node node = ShardConfig.getDefault().getNode(name, keys);
    return open(node.getRead());
  }

  /**
   * 打开分片数据库写节点
   *
   * @param name 数据库名称(在 db.sql.conf 中配置)
   * @param keys 分片参数
   */
  public static Database openWriteShard(String name, Object... keys) {
    ShardConfig.Node node = ShardConfig.getDefault().getNode(name, keys);
    return open(node.getWrite());
  }

  private static abstract class QueryImp implements Query, ConnectionManager {

    protected static final Logger logger = LoggerManager.getLogger(DatabaseImp.class);
    protected DataSource source;
    protected Builder builder;

    protected QueryImp(DataSource source, Builder builder) {
      this.source = source;
      this.builder = builder;
    }

    /**
     * 插入操作
     *
     * @param table 表名
     */
    @Override
    public InsertClause insert(String table) {
      return new InsertContext(this, this.builder, table);
    }

    /**
     * 插入操作
     *
     * @param obj 实体对象
     */
    @Override
    public InsertEndClause insert(Object obj) {
      return new InsertContext(this, this.builder, obj);
    }

    @Override
    public <T> InsertEndClause insert(List<T> objects) {
      return new InsertContext(this, this.builder, objects);
    }

    /**
     * 删除操作
     *
     * @param table 表名
     */
    @Override
    public DeleteClause delete(String table) {
      return new DeleteContext(this, builder, table);
    }

    /**
     * 删除操作
     *
     * @param obj 要删除的对象
     */
    @Override
    public DeleteEndClause delete(Object obj) {
      return new DeleteContext(this, builder, obj);
    }

    /**
     * 更新操作
     *
     * @param table 表名
     */
    @Override
    public UpdateClause update(String table) {
      return new UpdateContext(this, builder, table);
    }

    /**
     * 更新操作
     *
     * @param obj 实体对象
     */
    @Override
    public UpdateEndClause update(Object obj) {
      return new UpdateContext(this, builder, obj);
    }

    /**
     * 更新操作
     *
     * @param obj 实体对象
     * @param columns 更新列
     */
    @Override
    public UpdateEndClause update(Object obj, String... columns) {
      return new UpdateContext(this, builder, obj, columns);
    }

    @Override
    public SelectClause select(String column) {
      return new SelectContext(this, builder, new Columns(null, column));
    }

    /**
     * 查询操作
     *
     * @param columns 查询返回列
     */
    @Override
    public SelectClause select(String... columns) {
      return new SelectContext(this, builder, new Columns(null, columns));
    }

    /**
     * 查询操作
     *
     * @param columns 查询返回列
     */
    @Override
    public SelectClause select(Columns columns) {
      return new SelectContext(this, builder, columns);
    }

    /**
     * 查询操作
     *
     * @param clazz 查询映射对象类型
     */
    @Override
    public FromClause select(Class<?> clazz) {
      return new SelectContext(this, builder, clazz);
    }

    @Override
    public SelectEndClause select(Object obj) {
      return new SelectContext(this, builder, obj);
    }

    /**
     * 直接执行 SQL 语句
     *
     * @param sql SQL 语句
     * @param args 参数
     */
    @Override
    public ExecuteClause execute(String sql, Object... args) {
      return new ExecuteContext(this, sql, args);
    }

    /**
     * 直接执行 SQL 语句
     *
     * @param tpl SQL 模板
     * @param arg 参数, Map 或 POJO 对象
     */
    @Override
    public ExecuteClause execute(SqlTemplate tpl, Object arg) {
      BuildResult br = tpl.execute(arg);
      return new ExecuteContext(this, br.getSql(), br.getArgs().toArray(new Object[0]));
    }

    /**
     * 调用存储过程
     *
     * @param sp 存储过程名称
     */
    @Override
    public CallClause call(String sp) {
      return new CallContext(this, sp);
    }

    @Override
    public TableQuery table(String name, Object... keys) {
      ShardConfig.Node node = ShardConfig.getDefault().getNode(name, keys);
      if (node.getSuffix() == null) {
        throw new ConfigException("can't find suffix config for shard table: " + name);
      }
      return new TableQuery.TableQueryImp(name + node.getSuffix(), this, this.builder);
    }

    @Override
    public TableQuery table(Object obj) {
      Mapper.EntityInfo info = Mapper.getEntityInfo(obj.getClass());
      return table(info.table, info.getShardValues(obj));
    }
  }

  private static class DatabaseImp extends QueryImp implements Database {

    private DatabaseImp(DataSource source, Builder builder) {
      super(source, builder);
    }

    /**
     * 启动一个事务
     */
    @Override
    public Transaction begin() {
      return new TransactionImp(source, builder);
    }

    /**
     * 启动一个事务
     *
     * @param action 事务操作
     */
    @Override
    public void begin(Consumer<Transaction> action) {
      this.begin(action, false);
    }

    @Override
    public <T> T begin(Function<Transaction, T> func) {
      return this.begin(func, false);
    }

    @Override
    public <T> T begin(Function<Transaction, T> func, boolean setContext) {
      Transaction t = new TransactionImp(source, builder);

      if (setContext) {
        TransactionImp.current.set(t);
      }

      try {
        T result = func.apply(t);
        t.commit();
        return result;
      } catch (TxCancelledException e) {
        t.rollback();
        return null;
      } catch (Exception e) {
        t.rollback();
        throw e;
      } finally {
        t.close();
        if (setContext) {
          TransactionImp.current.remove();
        }
      }
    }

    /**
     * 启动一个事务
     *
     * @param action 事务操作
     * @param setContext 是否设置上下文事务
     */
    @Override
    public void begin(Consumer<Transaction> action, boolean setContext) {
      Transaction t = new TransactionImp(source, builder);

      if (setContext) {
        TransactionImp.current.set(t);
      }

      try {
        action.accept(t);
        t.commit();
      } catch (TxCancelledException e) {
        t.rollback();
      } catch (Exception e) {
        t.rollback();
        throw e;
      } finally {
        t.close();
        if (setContext) {
          TransactionImp.current.remove();
        }
      }
    }

    @Override
    public Connection getConnection() {
      try {
        return source.getConnection();
      } catch (SQLException e) {
        if (source instanceof BasicDataSource) {
          BasicDataSource ds = (BasicDataSource) source;
          logger.debug("db pool > max:{}, active:{}, idle:{}", ds.getMaxTotal(), ds.getNumActive(),
              ds.getNumIdle());
        }
        throw new JsdException(e);
      }
    }

    @Override
    public void closeConnection(Connection conn) {
      if (conn != null) {
        try {
          conn.close();
        } catch (Exception e) {
          throw new JsdException(e);
        }
      }
    }
  }

  static class TransactionImp extends QueryImp implements Transaction, AutoCloseable {

    static ThreadLocal<Transaction> current = new ThreadLocal<>();
    private Connection connection;
    private boolean completed;

    TransactionImp(DataSource source, Builder builder) {
      super(source, builder);
    }

    @Override
    public Connection getConnection() {
      try {
        if (this.connection == null) {
          this.connection = source.getConnection();
          this.connection.setAutoCommit(false);
        }
        return this.connection;
      } catch (SQLException e) {
        if (source instanceof BasicDataSource) {
          BasicDataSource ds = (BasicDataSource) source;
          logger.debug("db pool > max:{}, active:{}, idle:{}", ds.getMaxTotal(), ds.getNumActive(),
              ds.getNumIdle());
        }
        throw new JsdException(e);
      }
    }

    @Override
    public void closeConnection(Connection conn) {
      // connection will be closed after transaction closed
    }

//    public Savepoint save() {
//        try {
//            return connection.setSavepoint();
//        } catch (SQLException e) {
//            throw new JsdException(e);
//        }
//    }

    @Override
    public void rollback() {
      try {
        this.completed = true;
        if (this.connection != null) {
          this.connection.rollback();
          this.connection.setAutoCommit(true);
        }
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

//    public void rollback(Savepoint sp) {
//        try {
//            this.connection.rollback(sp);
////            this.connection.setAutoCommit(true);
//        } catch (SQLException e) {
//            throw new JsdException(e);
//        }
//    }

    @Override
    public void commit() {
      try {
        this.completed = true;
        this.connection.commit();
        this.connection.setAutoCommit(true);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
      // 如果事务没有提交或回滚则先回滚
      if (!completed) {
        try {
          this.rollback();
        } catch (Exception e) {
          logger.error("rollback failed", e);
        }
      }

      if (this.connection != null) {
        try {
          this.connection.close();
        } catch (SQLException e) {
          throw new JsdException(e);
        } finally {
          this.connection = null;
        }
      }
    }
  }

}
