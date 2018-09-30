package lucky.sky.net.rpc.simple.client;

import io.netty.channel.pool.ChannelPool;
import lucky.sky.net.rpc.config.ClientOptions;


public final class SimpleInvokerFactory {

  private static final SimpleClientChannelPoolMap poolMap = new SimpleClientChannelPoolMap();

  private SimpleInvokerFactory() {
    // 防止实例化
  }

  public static SimpleInvoker get(ClientOptions options) {
    SimpleClientOptions clientOptions = new SimpleClientOptions(options);
    ChannelPool pool = poolMap.get(clientOptions);
    return new SimpleInvoker(clientOptions, pool);
  }
}
