package lucky.sky.net.rpc.simple.server.handler;

import lucky.sky.net.rpc.simple.data.MsgType;
import lucky.sky.net.rpc.simple.handler.Decoder;


public class ServerDecoder extends Decoder {

  public ServerDecoder() {
    super(MsgType.REQUEST);
  }
}
