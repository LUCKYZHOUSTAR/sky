package lucky.sky.util.redis;

import lucky.sky.util.config.RedisConfig;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


/*
   sharedjedis：横向扩展redis
 */
public final class RedisClient {

  private static final Logger logger = LoggerManager
      .getLogger(RedisClient.class);
  private static Map<String, ShardedJedisPool> pools = new HashMap<>();
  private ShardedJedisPool pool;

  public RedisClient(String name) {
    this.pool = getPool(name);
  }

  private static synchronized ShardedJedisPool getPool(String name) {
    ShardedJedisPool pool = pools.get(name);
    if (pool != null) {
      return pool;
    }

    RedisConfig.RedisInfo di = RedisConfig.get(name);
    int minPoolSize = di.getSettings().getInt32("MinPoolSize", 1);
    int maxPoolSize = di.getSettings().getInt32("MaxPoolSize", 100);
    int connectTimeout = di.getSettings().getInt32("ConnectTimeout", 5000);
    int readTimeout = di.getSettings().getInt32("ReadTimeout", 5000);

    ArrayList<JedisShardInfo> shards = new ArrayList<>();
    List<RedisConfig.RedisNode> nodes = di.getServers();
    for (RedisConfig.RedisNode node : nodes) {
      JedisShardInfo shard = new JedisShardInfo(node.getHost(), node.getPort());
      if (connectTimeout > 0) {
        shard.setConnectionTimeout(connectTimeout);
      }
      if (readTimeout > 0) {
        shard.setSoTimeout(readTimeout);
      }

      shards.add(shard);
    }

    GenericObjectPoolConfig cfg = new GenericObjectPoolConfig();
    cfg.setMaxTotal(maxPoolSize);
    cfg.setMinIdle(minPoolSize);
    pool = new ShardedJedisPool(cfg, shards);
    pools.put(name, pool);
    return pool;
  }

  /**
   * 获取值
   */
  public String get(String key) {
    ShardedJedis jedis = pool.getResource();
    try {
      return jedis.get(key);
    } catch (JedisException e) {
      logger.error("set failed, server: {}, error: {}",
          jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 获取值
   */
  public byte[] getBytes(String key) {
    ShardedJedis jedis = pool.getResource();
    try {
      return jedis.get(key.getBytes(StandardCharsets.UTF_8));
    } catch (JedisException e) {
      logger.error("set failed, server: {}, error: {}",
          jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 设置值并返回之前的值
   */
  public String getSet(String key, String value) {
    ShardedJedis jedis = pool.getResource();
    try {
      return jedis.getSet(key, value);
    } catch (JedisException e) {
      logger.error("set failed, server: {}, error: {}",
          jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 添加缓存, 仅当键不存在时才会插入
   *
   * @param key 键
   * @param value 值
   */
  public boolean add(String key, String value) {
    ShardedJedis jedis = pool.getResource();
    try {
      long result = jedis.setnx(key, value);
      return result == 1;
    } catch (JedisException e) {
      logger.error("set failed, server: {}, error: {}",
          jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 设置缓存
   *
   * @param key 键
   * @param value 值
   */
  public String set(String key, String value) {
    ShardedJedis jedis = pool.getResource();
    try {
      return jedis.set(key, value);
    } catch (JedisException e) {
      logger.error("set failed, server: {}, error: {}",
          jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 设置缓存
   *
   * @param key 键
   * @param value 值
   * @param expiry 过期时间
   */
  public String set(String key, String value, Duration expiry) {
    ShardedJedis jedis = pool.getResource();
    try {
      return jedis.setex(key, (int) expiry.getSeconds(), value);
    } catch (JedisException e) {
      logger.error("set failed, server: {}, error: {}",
          jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 设置缓存
   *
   * @param key 键
   * @param value 值
   * @param expiry 过期时间
   */
  public String set(String key, byte[] value, Duration expiry) {
    ShardedJedis jedis = pool.getResource();
    try {
      return jedis.setex(key.getBytes(StandardCharsets.UTF_8), (int) expiry.getSeconds(), value);
    } catch (JedisException e) {
      logger.error("set failed, server: {}, error: {}",
          jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 是否存在缓存
   *
   * @param key 缓存键
   */
  public boolean exist(String key) {
    ShardedJedis jedis = pool.getResource();
    try {
      return jedis.exists(key);
    } catch (JedisException e) {
      logger.error("exist failed, server: {}, error: {}",
          jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 删除缓存
   *
   * @param key 缓存键
   */
  public void delete(String key) {
    ShardedJedis jedis = pool.getResource();
    try {
      jedis.del(key);
    } catch (JedisException e) {
      logger.error("delete failed, server: {}, error: {}",
          jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 增加缓存值 1
   *
   * @param key 缓存键
   */
  public Long increase(String key) {
    ShardedJedis jedis = pool.getResource();
    try {
      return jedis.incr(key);
    } catch (JedisException e) {
      logger.error("increase failed, server: {}, error: {}",
          jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 增加缓存值 value
   *
   * @param key 缓存键
   * @param value 增加的值
   */
  public Long increase(String key, long value) {
    ShardedJedis jedis = pool.getResource();
    try {
      return jedis.incrBy(key, value);
    } catch (JedisException e) {
      logger.error("increase failed, server: {}, error: {}", jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 设置缓存过期时间
   */
  public Long expire(String key, int seconds) {
    ShardedJedis jedis = pool.getResource();
    try {
      return jedis.expire(key, seconds);
    } catch (JedisException e) {
      logger.error("expire failed, server: {}, error: {}", jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 设置缓存过期时间
   */
  public Long expireAt(String key, long unixTime) {
    ShardedJedis jedis = pool.getResource();
    try {
      return jedis.expireAt(key, unixTime);
    } catch (JedisException e) {
      logger.error("expireAt failed, server: {}, error: {}", jedis.getShardInfo(key), e);
      throw e;
    } finally {
      jedis.close();
    }
  }

  /**
   * 执行自定义操作
   *
   * @param func 操作执行者
   */
  public <T> T invoke(Function<ShardedJedis, T> func) {
    ShardedJedis jedis = pool.getResource();
    try {
      return func.apply(jedis);
    } catch (JedisException e) {
      logger.error("invoke failed", e);
      throw e;
    } finally {
      jedis.close();
    }
  }
}
