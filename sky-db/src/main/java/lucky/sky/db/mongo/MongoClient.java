package lucky.sky.db.mongo;

import com.mongodb.DBObjectCodecProvider;
import com.mongodb.DBRefCodecProvider;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
//import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lucky.sky.db.config.ShardConfig;
import org.bson.Document;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.HashMap;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

/**
 */
public final class MongoClient {

  private static HashMap<String, MongoClient> clients = new HashMap<>();
  private com.mongodb.MongoClient client;
  private MongoDatabase db;

  public MongoClient(String name) {
    MongoConfig.MongoInfo info = MongoConfig.get(name);
    initialize(info);
  }

  public MongoClient(MongoConfig.MongoInfo info) {
    initialize(info);
  }

  private void initialize(MongoConfig.MongoInfo info) {
    CodecRegistry registry = fromProviders(
        new ObjectCodecProvider(), // 这里仿照MongoClient原始并增加自定义
        new ValueCodecProvider(),
        new DBRefCodecProvider(),
        new DocumentCodecProvider(),
        new DBObjectCodecProvider(),
        new BsonValueCodecProvider());
    MongoClientOptions.Builder builder = new MongoClientOptions.Builder().codecRegistry(registry);
    MongoClientURI uri = new MongoClientURI(info.getUrl(), builder);
    client = new com.mongodb.MongoClient(uri);
//        client.setWriteConcern(WriteConcern.NORMAL);
    db = client.getDatabase(uri.getDatabase());
  }

  public MongoDatabase getDatabase() {
    return this.db;
  }

  public MongoCollection<Document> getCollection(String name) {
    return db.getCollection(name);
  }

  public <T> MongoCollection<T> getCollection(String name, Class<T> tClass) {
    return db.getCollection(name, tClass);
  }

  /**
   * 重构泛型实现，减少参数
   */
  public <T> MongoCollection<T> getCollection(Class<T> tClass) {
    return db.getCollection(tClass.getSimpleName(), tClass);
  }

//    public void close() {
//        client.close();
//    }

  public synchronized static MongoClient open(String name) {
    MongoClient client = clients.get(name);
    if (client != null) {
      return client;
    }

    client = new MongoClient(name);
    clients.put(name, client);
    return client;
  }

  /**
   * 打开分片数据库读节点
   *
   * @param name 数据库名称(在 db.mongo.conf 中配置)
   * @param keys 分片参数
   */
  public static MongoClient openReadShard(String name, Object... keys) {
    ShardConfig.Node node = ShardConfig.getDefault().getNode(name, keys);
    return open(node.getRead());
  }

  /**
   * 打开分片数据库写节点
   *
   * @param name 数据库名称(在 db.mongo.conf 中配置)
   * @param keys 分片参数
   */
  public static MongoClient openWriteShard(String name, Object... keys) {
    ShardConfig.Node node = ShardConfig.getDefault().getNode(name, keys);
    return open(node.getWrite());
  }

}
