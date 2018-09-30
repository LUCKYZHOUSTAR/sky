package lucky.sky.util.config;

import lucky.sky.util.lang.StrKit;


public final class GlobalConfig {

  private static final String LUCKY_SERVER_IP_KEY = "LUCKY_SERVER_IP";

  private String nsqSubscribeAddress;
  private String zkAddress;
  private boolean rpcDiscoveryEnabled;
  private boolean rpcRegisterEnabled;
  private String rpcRegisterType;
  private String rpcRegisterIP;
  private String logPath;
  private int rpcClientReadTimeout;
  private boolean rpcClientStatsEnabled;
  private String rpcClientStatsType;
  private boolean rpcServerStatsEnabled;
  private String rpcServerStatsType;
  private String defaultActiveProfile;
  private String nameSrvAdd;

  GlobalConfig(SettingMap settings) {
    boolean isOnline = isOnlineEnv();
    logPath = settings.getString("log.path", isOnline ? "/home/lucky/logs" : "");
    nsqSubscribeAddress = settings.getString("nsq.subscribe.address", "");
    zkAddress = settings.getString("zk.address", "");
    rpcDiscoveryEnabled = settings.getBool("rpc.discovery.enabled", isOnline);
    rpcRegisterEnabled = settings.getBool("rpc.register.enabled", isOnline);
    rpcRegisterType = settings.getString("rpc.register.type", "zookeeper");
    rpcRegisterIP = settings.getString("rpc.register.ip", System.getenv(LUCKY_SERVER_IP_KEY));
    rpcClientStatsType = settings.getString("rpc.client.stats.type", "nsq");
    rpcClientStatsEnabled = settings.getBool("rpc.client.stats.enabled", false);
    rpcServerStatsType = settings.getString("rpc.server.stats.type", "log");
    rpcServerStatsEnabled = settings.getBool("rpc.server.stats.enabled", true);
    rpcClientReadTimeout = settings.getInt32("rpc.client.timeout.read", 30 * 1000);
    defaultActiveProfile = settings.getString("default.active.profile", "");
    nameSrvAdd = settings.getString("mq.name.srv", "");
  }

  private boolean isOnlineEnv() {
    String[] activeProfiles = ConfigProperties.getActiveProfiles();
    if (activeProfiles != null) {
      for (String p : activeProfiles) {
        if (p.endsWith("-prd") || p.endsWith("-stg")) {
          return true;
        }
      }
    }
    return false;
  }

  public String getNsqSubscribeAddress() {
    return nsqSubscribeAddress;
  }

  public String getZkAddress() {
    return zkAddress;
  }

  public boolean isRpcDiscoveryEnabled() {
    return rpcDiscoveryEnabled;
  }

  public boolean isRpcRegisterEnabled() {
    return rpcRegisterEnabled;
  }

  public String getRpcRegisterType() {
    return rpcRegisterType;
  }

  public String getRpcRegisterIP() {
    return rpcRegisterIP;
  }

  public boolean isRpcClientStatsEnabled() {
    return rpcClientStatsEnabled;
  }

  public String getRpcClientStatsType() {
    return rpcClientStatsType;
  }

  public boolean isRpcServerStatsEnabled() {
    return rpcServerStatsEnabled;
  }

  public String getRpcServerStatsType() {
    return rpcServerStatsType;
  }

  public String getLogPath() {
    return logPath;
  }

  public String getDefaultActiveProfile() {
    return defaultActiveProfile;
  }

  public void setDefaultActiveProfile(String defaultActiveProfile) {
    this.defaultActiveProfile = defaultActiveProfile;
  }

  public String getNameSrvAdd() {
    return nameSrvAdd;
  }

  public void setNameSrvAdd(String nameSrvAdd) {
    this.nameSrvAdd = nameSrvAdd;
  }

  /**
   * RPC 客户端默认读取超时时间, 单位毫秒
   */
  public int getRpcClientReadTimeout() {
    return rpcClientReadTimeout;
  }
}
