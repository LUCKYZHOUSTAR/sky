

package lucky.sky.net.rpc.simple.data;


public class JResponsePayload extends PayloadHolder {


  private String invokeId;


  public JResponsePayload() {

  }

  public String getInvokeId() {
    return invokeId;
  }

  public void setInvokeId(String invokeId) {
    this.invokeId = invokeId;
  }
}
