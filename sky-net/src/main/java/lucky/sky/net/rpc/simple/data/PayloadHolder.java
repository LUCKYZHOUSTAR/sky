

package lucky.sky.net.rpc.simple.data;


public abstract class PayloadHolder {


  private byte[] bytes;

  public byte[] bytes() {
    return bytes;
  }

  public void bytes(byte[] bytes) {
    this.bytes = bytes;
  }


  // help gc
  public void clear() {
    bytes = null;
  }

  public int size() {
    return (bytes == null ? 0 : bytes.length);
  }
}
