package lucky.sky.db.mongo;

import lombok.Getter;
import lucky.sky.db.AbstractEntity;
import lucky.sky.util.lang.Exceptions;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.lang.reflect.ParameterizedType;

/**
 * 支持读写分离、数据分片的通用 MongoDB 数据访问对象基类。
 *
 * TODO: not much stable, possible to change in future
 */
public class ReadWriteShardMgoDao<T extends AbstractEntity<K>, K> {

  @Getter
  private Class<T> entityClass;
  @Getter
  private String shardClusterName;

  /**
   * @param shardClusterName shard.conf 中的分片集群名称
   */
  public ReadWriteShardMgoDao(Class<T> entityClass, String shardClusterName) {
    this.shardClusterName = shardClusterName;
    this.entityClass = entityClass;
  }

  /**
   * @param shardClusterName shardClusterName shard.conf 中的分片集群名称
   */
  @SuppressWarnings("unchecked")
  protected ReadWriteShardMgoDao(String shardClusterName) {
    this.shardClusterName = shardClusterName;
    this.entityClass = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
        .getActualTypeArguments()[0]);
  }

  public Key<T> save(T entity) {
    Datastore db = getWriteShardDb(getShardKeys(entity));
    return db.save(entity);
  }

  /**
   * 根据主键更新
   */
  public UpdateResults updateById(K id, UpdateOperations<T> ops) {
    Datastore db = getWriteShardDb(id);
    Query<T> q = db.createQuery(entityClass).field("_id").equal(id);
    return db.update(q, ops);
  }

  /**
   * 获取指定主键的实体对象，如果不存在则返回 null。
   */
  public T get(K id) {
    return get(id, false);
  }

  /**
   * @param throwNotFound 如果不存在，是否抛出异常
   */
  public T get(K id, boolean throwNotFound) {
    Datastore db = getReadShardDb(id);
    T obj = db.get(entityClass, id);
    if (obj == null && throwNotFound) {
      throw Exceptions.notFoundObject(entityClass.getSimpleName(), id);
    }
    return obj;
  }

  /**
   * 获取分片只读数据库
   */
  protected Datastore getReadShardDb(Object... shardKeys) {
    return Mgo.getReadShardDb(shardClusterName, shardKeys);
  }

  /**
   * 获取分片可写数据库
   */
  protected Datastore getWriteShardDb(Object... shardKeys) {
    return Mgo.getWriteShardDb(shardClusterName, shardKeys);
  }

  /**
   * 获取指定实体对象的分片Key列表，默认返回对象主键。
   */
  protected Object[] getShardKeys(T entity) {
    return new Object[]{entity.getId()};
  }
}