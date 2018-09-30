package lucky.sky.net.rpc.simple.client.handler;

import lucky.sky.net.rpc.simple.data.MsgType;
import lucky.sky.net.rpc.simple.handler.Decoder;


public class ClientDecoder extends Decoder {

  public ClientDecoder() {
    super(MsgType.RESPONSE);
  }
}