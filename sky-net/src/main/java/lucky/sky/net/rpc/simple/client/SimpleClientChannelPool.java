package lucky.sky.net.rpc.simple.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lucky.sky.net.rpc.simple.client.handler.ClientDecoder;
import lucky.sky.net.rpc.simple.client.handler.ClientEncoder;
import lucky.sky.net.rpc.simple.client.handler.ClientHandler;
import lucky.sky.net.rpc.simple.client.handler.IdleHandler;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

public class SimpleClientChannelPool implements ChannelPool {

  private FixedChannelPool pool;

  private static final ClientHandler CLIENT_HANDLER = new ClientHandler();

  public SimpleClientChannelPool(SimpleClientOptions options) {
    Bootstrap client = createClient(options);
    pool = new FixedChannelPool(client,
        new SimpleChannelPoolHandler(options),
        ChannelHealthChecker.ACTIVE,
        FixedChannelPool.AcquireTimeoutAction.FAIL,
        options.getAcquireTimeout(),
        options.getMaxConnections(),
        options.getMaxPendingAcquires());
  }

  private Bootstrap createClient(SimpleClientOptions options) {
    Bootstrap bs = new Bootstrap();

    bs.group(new NioEventLoopGroup(options.getWorkThreads()));
    bs.channel(SimpleClientChannel.class);
    bs.remoteAddress(options.getAddress());

    // 设置传输设置
    bs.option(ChannelOption.SO_REUSEADDR, options.isReuseAddress());
    bs.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, options.getConnectTimeout());
    bs.option(ChannelOption.SO_SNDBUF, options.getSendBufferSize());
    bs.option(ChannelOption.SO_RCVBUF, options.getReceiveBufferSize());
    bs.option(ChannelOption.SO_KEEPALIVE, options.isKeepAlive());
    bs.option(ChannelOption.TCP_NODELAY, options.isTcpNoDelay());
    bs.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR,
        new DefaultMessageSizeEstimator(options.getReceiveBufferSize()));

    return bs;
  }

  @Override
  public Future<Channel> acquire() {
    return pool.acquire();
  }

  @Override
  public Future<Channel> acquire(Promise<Channel> promise) {
    return pool.acquire(promise);
  }

  @Override
  public Future<Void> release(Channel channel) {
    return pool.release(channel);
  }

  @Override
  public Future<Void> release(Channel channel, Promise<Void> promise) {
    return pool.release(channel, promise);
  }

  @Override
  public void close() {
    pool.close();
  }

  private static class SimpleChannelPoolHandler implements ChannelPoolHandler {

    private static Logger logger = LoggerManager.getLogger(SimpleChannelPoolHandler.class);
    private static final String DECODE = "decode";
    private static final String ENCODE = "encode";
    private static final String PROCESS = "process";
    private static final String IDLE_STATE = "idle_state";
    private static final String IDLE_PROCESS = "idle_process";

    private final SimpleClientOptions options;

    public SimpleChannelPoolHandler(SimpleClientOptions options) {
      this.options = options;
    }

    @Override
    public void channelReleased(Channel channel) throws Exception {
      if (logger.isTraceEnabled()) {
        logger.trace("channelReleased");
      }
    }

    @Override
    public void channelAcquired(Channel channel) throws Exception {
      if (logger.isTraceEnabled()) {
        logger.trace("channelAcquired");
      }
    }

    @Override
    public void channelCreated(Channel channel) throws Exception {
      if (logger.isTraceEnabled()) {
        logger.trace("channelCreated");
      }

      ChannelPipeline channelPipe = channel.pipeline();

      // write
      IdleStateHandler idleStateHandler = new IdleStateHandler(this.options.getKeepAliveTime(),
          this.options.getKeepAliveTime(), this.options.getKeepAliveTime());
      channelPipe.addFirst(IDLE_STATE, idleStateHandler);
      channelPipe.addFirst(IDLE_PROCESS, new IdleHandler());
      channelPipe.addFirst(ENCODE, new ClientEncoder());

      // read
      channelPipe.addLast(DECODE, new ClientDecoder());
      channelPipe.addLast(PROCESS, CLIENT_HANDLER);
    }
  }
}
