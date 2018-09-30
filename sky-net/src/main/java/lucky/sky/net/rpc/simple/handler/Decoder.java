package lucky.sky.net.rpc.simple.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lucky.sky.net.rpc.ProtocolException;
import lucky.sky.net.rpc.simple.data.JProtocolHeader;
import lucky.sky.net.rpc.simple.data.JRequestPayload;
import lucky.sky.net.rpc.simple.data.JResponsePayload;
import lucky.sky.net.rpc.simple.data.MsgType;

import java.util.List;

public abstract class Decoder extends ByteToMessageDecoder {

  private State state = State.MAGIC;
  private int length;
  private MsgType type;//请求的类型

  protected Decoder(MsgType type) {
    this.type = type;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    decode(in, out);
  }

  private void decode(ByteBuf buf, List<Object> out) throws Exception {
    switch (state) {
      case MAGIC:
        decodeMagic(buf, out);
        break;
      case SIGN:
        decodeSign(buf, out);
        break;
      case LENGTH:
        decodeLength(buf, out);
        break;
      case BODY:
        decodeBody(buf, out);
        break;
      default:
        throw new ProtocolException("invalid parse state: " + state);
    }
  }

  private void decodeBody(ByteBuf buf, List<Object> out) throws Exception {
    if (buf.readableBytes() >= length) {
      byte[] bytes = new byte[length];
      if (type.value() == MsgType.REQUEST.value()) {
        buf.readBytes(bytes);
        JRequestPayload jRequestPayload = new JRequestPayload();
        jRequestPayload.bytes(bytes);
        out.add(jRequestPayload);
      } else if (type.value() == MsgType.RESPONSE.value()) {
        buf.readBytes(bytes);
        //response
        JResponsePayload jResponsePayload = new JResponsePayload();
        jResponsePayload.bytes(bytes);
        out.add(jResponsePayload);
      }
      state = State.MAGIC;
      // 这里其实 netty 内部也会自动处理剩余消息
      if (buf.readableBytes() > 0) {
        decode(buf, out);
      }
    }
  }

  private void decodeLength(ByteBuf buf, List<Object> out) throws Exception {
    if (buf.readableBytes() >= 4) {
      length = buf.readInt();
      state = State.BODY;
      decode(buf, out);
    }
  }

  private void decodeSign(ByteBuf buf, List<Object> out) throws Exception {
    if (buf.readableBytes() >= 1) {
      buf.readByte();
      state = State.LENGTH;
      decode(buf, out);
    }
  }

  private void decodeMagic(ByteBuf buf, List<Object> out) throws Exception {

    if (buf.readableBytes() >= 2) {
      final Short magic = buf.readShort();
      if (magic != JProtocolHeader.MAGIC) {
        throw new ProtocolException("expect: " + JProtocolHeader.MAGIC);
      }
      state = State.SIGN;
      decode(buf, out);
    }
  }

  private enum State {
    MAGIC, SIGN, LENGTH, BODY
  }
}
