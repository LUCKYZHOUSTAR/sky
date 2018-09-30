package lucky.sky.util.encode;


public interface BinaryEncodable {

  byte[] encode();

  void decode(byte[] bytes);
}
