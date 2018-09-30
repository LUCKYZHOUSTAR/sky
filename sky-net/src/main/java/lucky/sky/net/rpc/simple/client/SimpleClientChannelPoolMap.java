package lucky.sky.net.rpc.simple.client;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;


public class SimpleClientChannelPoolMap implements Closeable {

  private final ConcurrentMap<InetSocketAddress, SimpleClientChannelPool> map = PlatformDependent
      .newConcurrentHashMap();

  public final SimpleClientChannelPool get(SimpleClientOptions options) {
    SimpleClientChannelPool pool = this.map
        .get(ObjectUtil.checkNotNull(options.getAddress(), "key"));
    if (pool == null) {
      pool = this.newPool(options);
      SimpleClientChannelPool old = this.map.putIfAbsent(options.getAddress(), pool);
      if (old != null) {
        pool.close();
        pool = old;
      }
    }

    return pool;
  }

  public final boolean remove(InetSocketAddress key) {
    SimpleClientChannelPool pool = this.map.remove(ObjectUtil.checkNotNull(key, "key"));
    if (pool != null) {
      pool.close();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public final void close() {
    Iterator iterator = this.map.keySet().iterator();
    while (iterator.hasNext()) {
      Object key = iterator.next();
      this.remove((InetSocketAddress) key);
    }
  }

  private SimpleClientChannelPool newPool(SimpleClientOptions options) {
    return new SimpleClientChannelPool(options);
  }

}
