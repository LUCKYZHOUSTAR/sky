package lucky.sky.net.rpc.simple.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lucky.sky.net.rpc.simple.data.JProtocolHeader;
import lucky.sky.net.rpc.simple.data.JRequestPayload;


@ChannelHandler.Sharable
public class ClientEncoder extends MessageToByteEncoder<JRequestPayload> {

  @Override
  protected void encode(ChannelHandlerContext ctx, JRequestPayload jRequestPayload, ByteBuf out)
      throws Exception {
    final byte[] bytes = jRequestPayload.bytes();
    out.writeShort(JProtocolHeader.MAGIC);
    out.writeByte(JProtocolHeader.REQUEST);
    out.writeInt(bytes.length);
    out.writeBytes(bytes);
  }
}