package lucky.sky.net.rpc.simple.client.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.WriteTimeoutException;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

/**
 * on 15/12/8.
 */
@Sharable
public class IdleHandler extends ChannelDuplexHandler {

  private static Logger logger = LoggerManager.getLogger(IdleHandler.class);

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent e = (IdleStateEvent) evt;
      if (e.state() == IdleState.WRITER_IDLE) {
        logger.info("channel [{} - {}] is timeout, will be closed.", ctx.channel(),
            ctx.channel().remoteAddress());
        ctx.fireExceptionCaught(WriteTimeoutException.INSTANCE);
        ctx.close();
      }
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }
}