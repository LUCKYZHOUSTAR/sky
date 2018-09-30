package lucky.sky.net.rpc.simple.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lucky.sky.net.rpc.simple.data.JProtocolHeader;
import lucky.sky.net.rpc.simple.data.JResponsePayload;


public class ServerEncoder extends MessageToByteEncoder<JResponsePayload> {

  @Override
  protected void encode(ChannelHandlerContext ctx, JResponsePayload jResponsePayload, ByteBuf out)
      throws Exception {
    //responsemessage
    final byte[] bytes = jResponsePayload.bytes();
    out.writeShort(JProtocolHeader.MAGIC);
    out.writeByte(JProtocolHeader.RESPONSE);
    out.writeInt(bytes.length);
    out.writeBytes(bytes);
  }
}
