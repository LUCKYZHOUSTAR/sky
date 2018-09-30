package lucky.sky.db.mongo;

import com.mongodb.*;
import com.mongodb.MongoClient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lucky.sky.db.AutoIncrementId;
import lucky.sky.db.DbEntity;
import lucky.sky.util.lang.Exceptions;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.bson.types.ObjectId;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.dao.DAO;
import org.mongodb.morphia.mapping.MapperOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用 MongoDB 数据访问对象基类。
 *
 * @param <T> 实体类型
 * @param <K> 实体主键类型
 */
@SuppressWarnings("unchecked")
public class MgoDao<T extends DbEntity<K>, K> {

  private static final Logger log = LoggerManager.getLogger(MgoDao.class);
  private DAO<T, K> dao;
  private boolean isAutoIncId;
  /**
   * 自增 ID 初始值
   */
  @Getter(AccessLevel.PROTECTED)
  @Setter(AccessLevel.PROTECTED)
  private int idSeed = 1;

  /**
   * Create a new BasicDAO
   *
   * @param entityClass the class of the POJO you want to persist using this DAO
   * @param mongoClient the representations of the connection to a MongoDB instance
   * @param morphia a Morphia instance
   * @param dbName the name of the database
   */
  public MgoDao(Class<T> entityClass, MongoClient mongoClient, Morphia morphia, String dbName) {
    this.dao = new BasicDAO<T, K>(entityClass, mongoClient, morphia, dbName);
    init();
  }

  /**
   * Create a new BasicDAO
   *
   * @param entityClass the class of the POJO you want to persist using this DAO
   * @param ds the Datastore which gives access to the MongoDB instance for this DAO
   */
  public MgoDao(Class<T> entityClass, Datastore ds) {
    this.dao = new BasicDAO<T, K>(entityClass, ds);
    init();
  }

  /**
   * Only calls this from your derived class when you explicitly declare the generic types with
   * concrete classes
   * <p/>
   * {@code class MyDao extends DAO<MyEntity, String>}
   *
   * @param mongoClient the representations of the connection to a MongoDB instance
   * @param morphia a Morphia instance
   * @param dbName the name of the database
   */
  protected MgoDao(MongoClient mongoClient, Morphia morphia, String dbName) {
    this.dao = new BasicDAO<T, K>((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
        .getActualTypeArguments()[0],
        mongoClient, morphia, dbName);
    init();
  }

  /**
   * @param ds
   */
  protected MgoDao(Datastore ds) {
    this.dao = new BasicDAO<T, K>(
        (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[0],
        ds);
    init();
  }

  /**
   * @param dbConfigName db.mongo.conf 中的 数据库配置
   */
  protected MgoDao(String dbConfigName) {
    this(Mgo.getDb(dbConfigName));
    init();
  }

  protected MgoDao(String dbConfigName, MapperOptions mapperOptions) {
    this(Mgo.getDb(dbConfigName));
    init();
  }

  protected DAO innerDao() {
    return dao;
  }

  protected void init() {
    isAutoIncId = getEntityClass().getAnnotation(AutoIncrementId.class) != null;
  }

  /**
   * Starts a update-operations def for this DAO entities type
   */
  protected UpdateOperations<T> createUpdateOperations() {
    return dao.createUpdateOperations();
  }

  protected MgoUpdater<T> createUpdater() {
    return new MgoUpdater<>(dao.createUpdateOperations());
  }

  /**
   * The type of entities for this DAO
   */
  protected Class<T> getEntityClass() {
    return dao.getEntityClass();
  }

  /**
   * 插入指定实体。 与 save 方法不同的是，save 方法对于已经存在的 key，将进行 update；而 insert 将抛出 DuplicateKeyException 异常。 对于具有
   * @AutoIncrementId 注解的类，将基于数据库中当前最大 _id 值计算下一个值，此实现目的是为提供简单、便捷的自增主键； 内部实现为乐观并发，即当冲突时进行尝试，默认尝试 10
   * 次，因此此模式不适合大数据量、高并发场景，此时请使用分布式发号器。
   */
  protected Key<T> insertEntity(T entity) {
    AdvancedDatastore ds = (AdvancedDatastore) dao.getDatastore();
    if (isAutoIncId) {
      return insertWithAutoIncId(ds, entity, null, null);
    } else {
      return ds.insert(entity);
    }
  }

  protected Key<T> insertEntity(T entity, WriteConcern wc) {
    AdvancedDatastore ds = (AdvancedDatastore) dao.getDatastore();
    if (isAutoIncId) {
      return insertWithAutoIncId(ds, entity, null, wc);
    } else {
      return ds.insert(entity, wc);
    }
  }

  protected Key<T> insertEntity(String kind, T entity) {
    AdvancedDatastore ds = (AdvancedDatastore) dao.getDatastore();
    if (isAutoIncId) {
      return insertWithAutoIncId(ds, entity, kind, null);
    } else {
      return ds.insert(kind, entity);
    }
  }

  /**
   * @param ds
   * @param entity
   * @param kind
   * @param wc
   * @return
   */
  private Key<T> insertWithAutoIncId(AdvancedDatastore ds, T entity, String kind, WriteConcern wc) {
    // 乐观并发控制，重试 10 次之后快速失败，避免极端情况下，饥饿死
    //
    // TODO: to read from config
    int retryTimes = 10;
    Integer nextId;
    while (retryTimes > 0) {
      Key<T> lastKey = ds.find(dao.getEntityClass()).retrievedFields(true, "_id").order("-_id")
          .limit(1).getKey();
      if (lastKey != null) {
        // TODO: how about Long?
        nextId = (Integer) lastKey.getId();
        nextId++;
      } else {
        nextId = idSeed;
      }
      entity.setId((K) nextId);
      //
      try {
        if (kind != null) {
          return ds.insert(kind, entity);
        } else if (wc != null) {
          return ds.insert(entity, wc);
        } else {
          return ds.insert(entity);
        }
      } catch (DuplicateKeyException ex) {
        log.warn("entity {} id {} exist", getEntityClass(), nextId);
        retryTimes--;
        if (retryTimes <= 0) {
          throw ex;
        }
      }
    }

    // should never reach here
    return null;
  }

  private Iterable<Key<T>> insertWithAutoIncId(AdvancedDatastore ds, T[] entities, String kind,
      WriteConcern wc) {
    List<Key<T>> keys = new ArrayList<>();
    for (T ent : entities) {
      Key<T> key = insertWithAutoIncId(ds, ent, kind, wc);
      keys.add(key);
    }
    return keys;
  }

  private Iterable<Key<T>> insertWithAutoIncId(AdvancedDatastore ds, Iterable<T> entities,
      String kind, WriteConcern wc) {
    List<Key<T>> keys = new ArrayList<>();
    for (T ent : entities) {
      Key<T> key = insertWithAutoIncId(ds, ent, kind, wc);
      keys.add(key);
    }
    return keys;
  }

  protected Iterable<Key<T>> insertEntities(T... entities) {
    AdvancedDatastore ds = (AdvancedDatastore) dao.getDatastore();
    if (isAutoIncId) {
      return insertWithAutoIncId(ds, entities, null, null);
    } else {
      return ds.insert(entities);
    }
  }

  protected Iterable<Key<T>> insertEntities(Iterable<T> entities, WriteConcern wc) {
    AdvancedDatastore ds = (AdvancedDatastore) dao.getDatastore();
    if (isAutoIncId) {
      return insertWithAutoIncId(ds, entities, null, wc);
    } else {
      return ds.insert(entities, wc);
    }
  }

  protected Iterable<Key<T>> insertEntities(String kind, Iterable<T> entities) {
    AdvancedDatastore ds = (AdvancedDatastore) dao.getDatastore();
    if (isAutoIncId) {
      return insertWithAutoIncId(ds, entities, kind, null);
    } else {
      return ds.insert(kind, entities);
    }
  }

  protected Iterable<Key<T>> insertEntities(String kind, Iterable<T> entities, WriteConcern wc) {
    AdvancedDatastore ds = (AdvancedDatastore) dao.getDatastore();
    if (isAutoIncId) {
      return insertWithAutoIncId(ds, entities, kind, wc);
    } else {
      return ds.insert(kind, entities, wc);
    }
  }

  /**
   * Saves the entity; either inserting or overriding the existing document
   */
  protected Key<T> save(T entity) {
    return dao.save(entity);
  }

  /**
   * Saves the entity; either inserting or overriding the existing document
   */
  protected Key<T> save(T entity, WriteConcern wc) {
    return dao.save(entity, wc);
  }

  /**
   * Updates the first entity matched by the constraints with the modifiers supplied.
   */
  protected UpdateResults updateFirst(Query<T> q, UpdateOperations<T> ops) {
    UpdateOperations<T> realOps =
        (ops instanceof MgoUpdater) ? ((MgoUpdater) ops).updateOperations() : ops;
    return dao.updateFirst(q, realOps);
  }

  /**
   * Updates all entities matched by the constraints with the modifiers supplied.
   */
  protected UpdateResults update(Query<T> q, UpdateOperations<T> ops) {
    UpdateOperations<T> realOps =
        (ops instanceof MgoUpdater) ? ((MgoUpdater) ops).updateOperations() : ops;
    return dao.update(q, realOps);
  }

  /**
   * Deletes the entity
   */
  protected WriteResult delete(T entity) {
    return dao.delete(entity);
  }

  /**
   * Deletes the entity
   */
  protected WriteResult delete(T entity, WriteConcern wc) {
    return dao.delete(entity, wc);
  }

  /**
   * Delete the entity by id value
   */
  protected WriteResult deleteById(K id) {
    return dao.deleteById(id);
  }

  /**
   * Saves the entities given the query
   */
  protected WriteResult deleteByQuery(Query<T> q) {
    return dao.deleteByQuery(q);
  }

  protected Query<T> createQuery() {
    return dao.createQuery();
  }

  protected MgoQuery<T> createMgoQuery() {
    return new MgoQuery(dao.createQuery());
  }

  /**
   * 根据主键获取实体对象
   */
  public T get(K id) {
    return dao.get(id);
  }

  /**
   * @param id
   * @param throwNotFound
   * @return
   */
  public T get(K id, boolean throwNotFound) {
    T obj = dao.get(id);
    if (obj == null && throwNotFound) {
      throw Exceptions.notFoundObject(dao.getEntityClass().getSimpleName(), id);
    }
    return obj;
  }

  public T getByObjectId(String hexStringId, boolean throwNotFound) {
    T obj = dao.getDatastore().get(dao.getEntityClass(), new ObjectId(hexStringId));
    if (obj == null && throwNotFound) {
      throw Exceptions.notFoundObject(dao.getEntityClass().getSimpleName(), hexStringId);
    }
    return obj;
  }

  /**
   * Finds the entities Ts
   */
  public List<K> findIds() {
    return dao.findIds();
  }

  /**
   * Finds the entities Key<T> by the criteria {key:value}
   */
  public List<K> findIds(String key, Object value) {
    return dao.findIds(key, value);
  }

  /**
   * Finds the entities Ts by the criteria {key:value}
   */
  protected List<K> findIds(Query<T> q) {
    return dao.findIds(q);
  }

  /**
   * Finds the first entity's ID
   */
  protected Key<T> findOneId() {
    return dao.findOneId();
  }

  /**
   * Finds the first entity's ID
   */
  protected Key<T> findOneId(String key, Object value) {
    return dao.findOneId(key, value);
  }

  /**
   * Finds the first entity's ID
   */
  protected Key<T> findOneId(Query<T> q) {
    return dao.findOneId(q);
  }

  /**
   * checks for entities which match criteria {key:value}
   */
  public boolean exists(String key, Object value) {
    return dao.exists(key, value);
  }

  /**
   * checks for entities which match the criteria
   */
  protected boolean exists(Query<T> q) {
    return dao.exists(q);
  }

  /**
   * returns the total count
   */
  public long count() {
    return dao.count();
  }

  /**
   * returns the count which match criteria {key:value}
   */
  public long count(String key, Object value) {
    return dao.count(key, value);
  }

  /**
   * returns the count which match the criteria
   */
  protected long count(Query<T> q) {
    return dao.count(q);
  }

  /**
   * returns the entity which match criteria {key:value}
   */
  public T findOne(String key, Object value) {
    return dao.findOne(key, value);
  }

  /**
   * returns the entity which match the criteria
   */
  protected T findOne(Query<T> q) {
    return dao.findOne(q);
  }

  /**
   * returns the entities
   */
  protected QueryResults<T> find() {
    return dao.find();
  }

  /**
   * returns the entities which match the criteria
   */
  protected QueryResults<T> find(Query<T> q) {
    return dao.find(q);
  }

  /**
   * ensures indexed for this DAO
   */
  protected void ensureIndexes() {
    dao.ensureIndexes();
  }

  /**
   * gets the collection
   */
  protected DBCollection getCollection() {
    return dao.getCollection();
  }

  /**
   * returns the underlying datastore
   */
  protected Datastore getDatastore() {
    return dao.getDatastore();
  }

  /**
   * 根据主键更新
   */
  protected UpdateResults updateById(K id, UpdateOperations<T> ops) {
    Query<T> q = createQuery().field("_id").equal(id);
    return this.updateFirst(q, ops);
  }

  /**
   * 根据主键更新
   *
   * @param hexStringId 24 位字符串表示的 ObjectId
   */
  protected UpdateResults updateByObjectId(String hexStringId, UpdateOperations<T> ops) {
    Query<T> q = createQuery().field("_id").equal(new ObjectId(hexStringId));
    return this.update(q, ops);
  }
}
