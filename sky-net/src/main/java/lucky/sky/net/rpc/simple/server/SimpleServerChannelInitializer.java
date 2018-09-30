package lucky.sky.net.rpc.simple.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import lucky.sky.net.rpc.simple.server.handler.*;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;


public class SimpleServerChannelInitializer extends ChannelInitializer<Channel> {

  private static Logger logger = LoggerManager.getLogger(SimpleServerChannelInitializer.class);

  /**
   * decode handler
   */
  private static final String DECODE = "decode";
  private static final String ENCODE = "encode";
  private static final String SESSION = "session";
  private static final String PROCESS = "process";
  private static final String IDLE_STATE = "idle_state";
  private static final String IDLE_PROCESS = "idle_process";

  private final SimpleServer server;
  private final SimpleServerOptions options;

  public SimpleServerChannelInitializer(SimpleServer server) {
    this.server = server;
    this.options = server.getOptions();
  }

  @Override
  protected void initChannel(Channel ch) throws Exception {
    logger.debug("initialize channel");

    SessionHandler sessionHandler = server.getSessionHandler();
    if (sessionHandler.getChannels().size() >= options.getMaxClients()) {
      logger.warn("reach max clients {}, close connection", options.getMaxClients());
      ch.close();
      return;
    }

    ChannelPipeline channelPipe = ch.pipeline();

    // read
    IdleStateHandler idleStateHandler = new IdleStateHandler(this.options.getKeepAliveTime(),
        this.options.getKeepAliveTime(), this.options.getKeepAliveTime());
    channelPipe.addLast(IDLE_STATE, idleStateHandler);
    channelPipe.addLast(IDLE_PROCESS, new IdleHandler());
    channelPipe.addLast(SESSION, sessionHandler);
    channelPipe.addLast(DECODE, new ServerDecoder());
    channelPipe.addLast(PROCESS, new ServerHandler(server));

    // write
    channelPipe.addFirst(ENCODE, new ServerEncoder());
  }
}
