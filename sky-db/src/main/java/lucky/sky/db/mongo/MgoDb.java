package lucky.sky.db.mongo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

/**
 * 表示基于 Morphia 的 MongoDB 数据库对象。 TODO: impl Datastore interface
 */
public class MgoDb {

  private Datastore ds;

  public MgoDb(Datastore ds) {
    this.ds = ds;
  }

  public Datastore datastore() {
    return ds;
  }

  /**
   * 创建指定 collection 的查询器。
   */
  @SuppressWarnings("unchecked")
  public <T> MgoQuery<T> createQuery(Class<T> collectionClass) {
    return new MgoQuery(ds.createQuery(collectionClass));
  }

  /**
   * 根据主键更新
   */
  public <T> UpdateResults updateById(Class<T> collectionClass, Object id,
      UpdateOperations<T> ops) {
    return updateById(ds, collectionClass, id, ops);
  }

  /**
   * 根据主键更新
   *
   * @param hexStringId 24 位字符串表示的 ObjectId
   */
  public <T> UpdateResults updateByObjectId(Class<T> collectionClass, String hexStringId,
      UpdateOperations<T> ops) {
    return updateById(ds, collectionClass, new ObjectId(hexStringId), ops);
  }

  /**
   * 根据主键更新
   */
  public static <T> UpdateResults updateById(Datastore ds, Class<T> collectionClass, Object id,
      UpdateOperations<T> ops) {
    Query<T> q = ds.createQuery(collectionClass)
        .field("_id").equal(id);
    return ds.update(q, ops);
  }

  /**
   * 根据主键更新
   *
   * @param hexStringId 24 位字符串表示的 ObjectId
   */
  public static <T> UpdateResults updateByObjectId(Datastore ds, Class<T> collectionClass,
      String hexStringId, UpdateOperations<T> ops) {
    return updateById(ds, collectionClass, new ObjectId(hexStringId), ops);
  }
}
