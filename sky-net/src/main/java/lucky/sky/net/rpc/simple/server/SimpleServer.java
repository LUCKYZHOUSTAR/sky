package lucky.sky.net.rpc.simple.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lucky.sky.net.rpc.Server;
import lucky.sky.net.rpc.ServiceContainer;
import lucky.sky.net.rpc.simple.server.handler.SessionHandler;
import lucky.sky.net.rpc.simple.server.service.*;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

import java.util.Date;
import java.util.concurrent.*;

/**
 *
 */
public class SimpleServer extends ServerBootstrap implements Server {

  private static final Logger logger = LoggerManager.getLogger(SimpleServer.class);

  private ServiceContainer container;
  private SimpleServerOptions options;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private final ThreadPoolExecutor threadPool;
  private SessionHandler sessionHandler = new SessionHandler();
  private Date startTime;

  private SimpleServerChannelInitializer handler;

  public SimpleServer(SimpleServerOptions options) {
    this.options = options;

    BlockingQueue<Runnable> queue = (options.getTaskQueueSize() == 0) ? new SynchronousQueue<>()
        : new LinkedBlockingQueue<>(options.getTaskQueueSize());
    threadPool = new ThreadPoolExecutor(options.getMinThreads(), options.getMaxThreads(), 120,
        TimeUnit.SECONDS, queue);
    this.bossGroup = new NioEventLoopGroup(options.getAcceptThreads());
    this.workerGroup = new NioEventLoopGroup(options.getWorkThreads());
    this.group(this.bossGroup, this.workerGroup);
    this.channel(NioServerSocketChannel.class);

    // worker option
    this.childOption(ChannelOption.SO_KEEPALIVE, options.isKeepAlive());
    this.childOption(ChannelOption.SO_REUSEADDR, true);
    this.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    this.childOption(ChannelOption.TCP_NODELAY, options.isTcpNoDelay());
    this.childOption(ChannelOption.SO_LINGER, options.getLinger());
    this.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, options.getConnectTimeout());
    this.childOption(ChannelOption.SO_RCVBUF, options.getReceiveBufferSize());
    this.childOption(ChannelOption.SO_SNDBUF, options.getSendBufferSize());

    this.container =
        options.isMethodNameIgnoreCase() ? new ServiceContainer.IgnoreCaseServiceContainer()
            : new ServiceContainer();
    // register system and test service
    this.container.registerService(SystemService.class, new SystemServiceImp(this));
    this.container.registerService(MetaService.class, new MetaServiceImp(this));
    this.container.registerService(TestService.class, new TestServiceImp());

    this.handler = new SimpleServerChannelInitializer(this);
    this.childHandler(handler);
  }

  @Override
  public void registerService(Object instance) {
    this.container.registerService(instance);
  }

  @Override
  public void registerService(Class<?> clazz, Object instance) {
    this.container.registerService(clazz, instance);
  }

  @Override
  public void start() {
    startTime = new Date();
    logger.info("rpc server starting at: " + options.getAddress());
    this.bind(options.getAddress()).addListener(future -> {
      if (future.isSuccess()) {
        logger.info("rpc server start success");
      } else {
        logger.error("rpc server start failed: ", future.cause());
        System.exit(1);
      }
    });
  }

  @Override
  public void stop() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object getData(String key) {
    switch (key) {
      case "clients.active":
        return this.sessionHandler.getChannels().size();
      case "clients.max":
        return this.options.getMaxClients();
      case "threads.active":
        return this.threadPool.getActiveCount();
      case "threads.max":
        return this.options.getMaxThreads();
      default:
        return null;
    }
  }

  public ServiceContainer getContainer() {
    return container;
  }

  public SimpleServerOptions getOptions() {
    return options;
  }

  public SessionHandler getSessionHandler() {
    return this.sessionHandler;
  }

  public ThreadPoolExecutor getThreadPool() {
    return threadPool;
  }

  public Date getStartTime() {
    return startTime;
  }
}
