package lucky.sky.net.rpc.simple.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lucky.sky.net.rpc.simple.client.DefaultConsumerProcessor;
import lucky.sky.net.rpc.simple.data.JResponsePayload;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;


@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<JResponsePayload> {

  private static final Logger logger = LoggerManager.getLogger(ClientHandler.class);


  @Override
  protected void channelRead0(ChannelHandlerContext ctx, JResponsePayload jResponsePayload)
      throws Exception {
    DefaultConsumerProcessor.handleResponse(jResponsePayload);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.close();
    // 针对不同的异常类型做处理?
    logger.error("unknown client error", cause);
  }


}