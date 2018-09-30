package lucky.sky.net.rpc;

import lucky.sky.util.data.Guid;

/**
 * on 15/12/28.
 */
public class RpcContext {

  private static final ThreadLocal<ContextData> data = ThreadLocal.withInitial(ContextData::new);

  private RpcContext() {
    // 防止实例化
  }

  /**
   * 设置上下文信息
   */
  public static void set(String contextID, String parentID) {
    ContextData contextData = data.get();
    contextData.contextID = contextID;
    contextData.parentID = parentID;
  }

  /**
   * 清除上下文信息
   */
  public static void remove() {
    ContextData contextData = data.get();
    contextData.contextID = null;
    contextData.parentID = null;
  }

  /**
   * 获取上下文 ID
   */
  public static String getContextID() {
    String id = data.get().contextID;
    return (id == null) ? Guid.get() : id;
  }

  /**
   * 设置上下文 ID
   */
  public static void setContextID(String id) {
    data.get().contextID = id;
  }

  /**
   * 清除上下文 ID
   */
  public static void removeContextID() {
    data.get().contextID = null;
  }

  /**
   * 获取父消息 ID
   */
  public static String getParentID() {
    String id = data.get().parentID;
    return (id == null) ? "" : id;
  }

  /**
   * 设置父消息 ID
   */
  public static void setParentID(String id) {
    data.get().parentID = id;
  }

  /**
   * 清除父消息 ID
   */
  public static void removeParentID() {
    data.get().parentID = null;
  }

  private static class ContextData {

    private String contextID;
    private String parentID;
  }
}
