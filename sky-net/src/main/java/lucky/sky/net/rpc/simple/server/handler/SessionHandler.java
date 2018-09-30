package lucky.sky.net.rpc.simple.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lucky.sky.net.rpc.simple.server.ChannelState;


@Sharable
public class SessionHandler extends ChannelInboundHandlerAdapter {

  private DefaultChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

  public DefaultChannelGroup getChannels() {
    return channels;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    Channel channel = ctx.channel();
    channel.attr(ChannelState.KEY).set(new ChannelState());
    channels.add(channel);
  }
}
