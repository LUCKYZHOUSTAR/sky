package lucky.sky.net.rpc;

import lucky.sky.net.rpc.simple.client.SimpleClientOptions;
import lucky.sky.util.config.ConfigProperties;
import lucky.sky.util.lang.FaultException;
import org.apache.commons.lang3.text.StrBuilder;

import java.util.List;

/**
 * RPC 错误异常相关实用类。
 */
public class RpcExceptions {

  private RpcExceptions() {
    // 防止实例化
  }

  /**
   * 自定义错误信息键名。
   */
  public static class CustomErrorDataKeys {

    public static final String SERVER_NAME = "serverName";
    public static final String SERVER_ADDRESS = "serverAddress";
    public static final String REMOTE_ERROR = "remoteError";
    public static final String NODE_ERRORS = "nodeErrors";
    public static final String PROFILE = "profile";

    private CustomErrorDataKeys() {
      // 防止实例化
    }
  }

  public static void putRemoteError(FaultException fault, String remoteException) {
    fault.getData().put(CustomErrorDataKeys.REMOTE_ERROR, remoteException);
  }

  public static void putRemoteServer(FaultException fault, SimpleClientOptions clientOptions) {
    fault.getData().put(CustomErrorDataKeys.SERVER_NAME, clientOptions.getName());
    fault.getData().put(CustomErrorDataKeys.SERVER_ADDRESS, clientOptions.getAddress().toString());
  }

  public static void putNodeFaults(FaultException fault, List<FaultException> faultExceptions) {
    StrBuilder sb = new StrBuilder();
    sb.appendNewLine();
    for (FaultException e : faultExceptions) {
      sb.appendln(e.toStringDetail());
    }
    fault.getData().put(CustomErrorDataKeys.NODE_ERRORS, sb.toString());
  }

  public static void putProfile(FaultException fault) {
    fault.getData().put(CustomErrorDataKeys.PROFILE, ConfigProperties.activeProfiles());
  }
}
