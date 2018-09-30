package lucky.sky.db.mongo;

import com.google.common.base.Strings;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import lucky.sky.db.config.ShardConfig;
import lucky.sky.db.mongo.codecs.LocalDateTimeCodec;
import lucky.sky.db.mongo.converters.*;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.convert.StringConverter;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.converters.Converters;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.mapping.MapperOptions;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 封装 MongoDB 实用功能。
 */
public class Mgo {

  private static final Logger log = LoggerManager.getLogger(Mgo.class);
  private static ConcurrentMap<String, MongoClient> clientCache = new ConcurrentHashMap<>();
  private static ConcurrentMap<String, Datastore> dsCache = new ConcurrentHashMap<>();
  /**
   * 是否使用小写类名作为集合名，默认为 true
   */
  public static final boolean useLowerCaseCollectionName = true;

  /**
   * 从指定的数据库配置节初始化 MongoClient。
   */
  public static MongoClient getMongo(String dbConfigName) {
    return getMongo(newMongoURI(dbConfigName));
  }

  public static MongoClient getMongo(MongoClientURI uri) {
    String url = uri.getURI();
    MongoClient client = clientCache.get(url);
    if (client != null) {
      return client;
    }
    client = new MongoClient(uri);
    MongoClient tmp = clientCache.putIfAbsent(url, client);
    if (tmp != null) {
      return tmp;
    }
    return client;
  }

  private static MongoClientURI newMongoURI(String dbConfigName) {
    MongoConfig.MongoInfo info = MongoConfig.get(dbConfigName);
    return newMongoClientURI(info.getUrl());
  }

  private static MongoClientURI newMongoClientURI(String connectionStringUrl) {
    CodecRegistry codecRegistry = CodecRegistries
        .fromRegistries(CodecRegistries.fromCodecs(new LocalDateTimeCodec()),
            MongoClient.getDefaultCodecRegistry());
    MongoClientOptions.Builder optsBld = MongoClientOptions.builder().codecRegistry(codecRegistry);
    return new MongoClientURI(connectionStringUrl, optsBld);
  }

  /**
   * 使用指定的连接字符串初始化 MongClient 。 格式：mongodb://host:port/database
   */
  public static MongoClient getMongoClient(String connectionStringUrl) {
    return getMongo(newMongoClientURI(connectionStringUrl));
  }

  public static Morphia newMorphia(String... mapPackages) {
    return newMorphiaInternal(null, mapPackages);
  }

  public static Morphia newMorphia(MapperOptions mapperOptions, String... mapPackages) {
    return newMorphiaInternal(mapperOptions, mapPackages);
  }

  private static Morphia newMorphiaInternal(MapperOptions mapperOptions, String... mapPackages) {
    Morphia morphia = new Morphia();

    //
    Mapper mapper = morphia.getMapper();
    if (mapperOptions != null) {
      mapper.setOptions(mapperOptions);
    } else {
      mapper.getOptions().setUseLowerCaseCollectionNames(useLowerCaseCollectionName);
    }

    //
    Converters converters = mapper.getConverters();
    converters.addConverter(LocalDateTimeConverter.class);
    converters.addConverter(LocalDateConverter.class);
    converters.addConverter(LocalTimeConverter.class);
    converters.addConverter(EnumValueConverter.class);
    converters.addConverter(DayOfWeekConverter.class);

    //
    if (mapPackages != null && mapPackages.length > 0) {
      for (String pkg : mapPackages) {
        morphia.mapPackage(pkg);
      }
    }
    return morphia;
  }

  public static Datastore getDatabase(String dbConfigName, String dbName, String... mapPackages) {
    return getDbInternal(dbConfigName, dbName, null, null, mapPackages);
  }

  public static Datastore getDb(String dbConfigName, String dbName, MapperOptions mapperOptions,
      String... mapPackages) {
    return getDbInternal(dbConfigName, dbName, null, mapperOptions, mapPackages);
  }

  public static Datastore getDb(String dbConfigName, String dbName, Morphia morphia) {
    return getDbInternal(dbConfigName, dbName, morphia, null);
  }

  public static Datastore getDb(String dbConfigName, String... mapPackages) {
    return getDbInternal(dbConfigName, null, null, null, mapPackages);
  }

  public static Datastore getDb(String dbConfigName, MapperOptions mapperOptions) {
    return getDbInternal(dbConfigName, null, null, mapperOptions);
  }

  public static Datastore getDb(String dbConfigName, Morphia morphia) {
    return getDbInternal(dbConfigName, null, morphia, null);
  }

  public static Datastore getDb(String dbConfigName) {
    return getDbInternal(dbConfigName, null, null, null);
  }

  private static Datastore getDbInternal(String dbConfigName, String dbName, Morphia morphia,
      MapperOptions mapperOptions, String... mapPackages) {
    Objects.requireNonNull(dbConfigName, "arg dbConfigName");
    //
    String dbKey = dbConfigName + "|" + dbName;
    Datastore db = dsCache.get(dbKey);
    if (db != null) {
      return db;
    }

    // first reach here, create a new instance
    //
    MongoClientURI uri = newMongoURI(dbConfigName);
    MongoClient client = getMongo(uri);
    String validName = dbName;
    if (validName == null) {
      // try to find db from connection string
      validName = uri.getDatabase();
      if (Strings.isNullOrEmpty(validName)) {
        throw new IllegalArgumentException(
            "can not found db from connection string: " + dbConfigName);
      }
    }
    //
    Morphia mfa = morphia;
    if (mfa == null) {
      mfa = newMorphiaInternal(mapperOptions, mapPackages);
    }
    db = mfa.createDatastore(client, validName);
    Datastore tmp = dsCache.putIfAbsent(dbKey, db);
    if (tmp != null) {
      // return the possible existing instance.
      return tmp;
    }

    //
    return db;
  }

  public static Datastore getShardDb(String shardClusterName, DbAccessMode mode, Object... keys) {
    ShardConfig.Node node = ShardConfig.getDefault().getNode(shardClusterName, keys);
    String dbCfgName = mode == DbAccessMode.READ ? node.getRead() : node.getWrite();
    if (log.isDebugEnabled()) {
      log.debug("shard node of db:{},keys:{},mode:{} is: {}", shardClusterName,
          StringConverter.toString(",", keys), mode, dbCfgName);
    }
    return getDb(dbCfgName);
  }

  public static Datastore getReadShardDb(String shardClusterName, Object... keys) {
    return getShardDb(shardClusterName, DbAccessMode.READ, keys);
  }

  public static Datastore getWriteShardDb(String shardClusterName, Object... keys) {
    return getShardDb(shardClusterName, DbAccessMode.WRITE, keys);
  }

  public static MgoDb getMgoDb(String dbConfigName) {
    return new MgoDb(getDb(dbConfigName));
  }

  public enum DbAccessMode {
    READ, WRITE
  }
}

