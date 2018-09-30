package lucky.sky.net.rpc.simple.server.service;

import lucky.sky.net.rpc.annotation.RpcMethod;
import lucky.sky.net.rpc.annotation.RpcService;

import java.util.List;

/**
 *
 */
@RpcService(name = "$system", description = "系统服务")
public interface SystemService {

  /**
   * 心跳检测
   *
   * @return 是否成功
   */
  @RpcMethod(name = "Ping", description = "心跳检测")
  boolean ping();

  /**
   * 获取服务器信息
   *
   * @return 服务器状态信息
   */
  @RpcMethod(name = "GetInfo", description = "获取服务程序信息")
  GetInfoResponse getInfo();

  /**
   * 获取当前连接的客户端列表
   *
   * @return 客户端列表
   */
  @RpcMethod(name = "GetClientList", description = "获取当前连接的客户端列表")
  GetClientListResponse getClientList();

  /**
   * 获取服务端信息接口返回值
   */
  class GetInfoResponse {

    // 服务名称
    public String name;

    // 版本
    public String version;

    // 启动时间
    public long startTime;

    // 当前连接数
    public int clients;

    // 最大连接数
    public int maxClients;

    // 构建信息，如版本号，构建时间等, 以 json 格式返回
    public String buildInfo;

    /**
     * 当前的配置环境
     */
    public String profiles;
  }

  /**
   * 获取客户端列表接口返回值
   */
  class GetClientListResponse {

    // 客户端列表
    public List<ClientInfo> clients;

    public static class ClientInfo {

      // ID
      public String id;

      // 地址
      public String address;

      // 创建时间
      public long createTime;

      // 上次活跃时间
      public long activeTime;

      // 上次调用服务
      public String service;

      // 上次调用方法
      public String method;
    }
  }
}
