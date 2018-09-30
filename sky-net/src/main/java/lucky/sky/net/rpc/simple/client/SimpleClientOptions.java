package lucky.sky.net.rpc.simple.client;

import lucky.sky.net.rpc.config.ClientOptions;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.config.SettingMap;

import java.net.InetSocketAddress;


public class SimpleClientOptions {

  private static final int READ_TIMEOUT = 30 * 1000;
  private static final int WRITE_TIMEOUT = 30 * 1000;
  private static final int CONNECT_TIMEOUT = 10 * 1000;
  private static final int ACQUIRE_TIMEOUT = 10 * 1000;

  private String name;
  private InetSocketAddress address;
  private int workThreads;
  private boolean reuseAddress;
  private int connectTimeout = CONNECT_TIMEOUT;
  private int acquireTimeout = ACQUIRE_TIMEOUT;
  private int readTimeout = READ_TIMEOUT;
  private int writeTimeout = WRITE_TIMEOUT;
  private int maxConnections = 500;
  private int maxPendingAcquires = 100;
  private int receiveBufferSize = 1024 * 64;
  private int sendBufferSize = 1024 * 64;
  private boolean tcpNoDelay;
  private boolean keepAlive;
  private int keepAliveTime = 30 * 60;  // 单位秒, 默认 30 分钟

  public SimpleClientOptions(ClientOptions options) {
    this.name = options.getName();

    String[] parts = options.getAddress().split(":");
    if (parts.length != 2) {
      throw new IllegalArgumentException("invalid net address: " + options.getAddress());
    }
    this.address = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));

    SettingMap settings = options.getSettings();
    this.maxConnections = getIntValue(settings, "MaxConnections", 500, 500);
    this.connectTimeout = getIntValue(settings, "ConnectTimeout", CONNECT_TIMEOUT, CONNECT_TIMEOUT);
    this.readTimeout = getIntValue(settings, "ReadTimeout",
        AppConfig.getDefault().getGlobal().getRpcClientReadTimeout(), READ_TIMEOUT);
    this.writeTimeout = getIntValue(settings, "WriteTimeout", WRITE_TIMEOUT, WRITE_TIMEOUT);
  }

  private int getIntValue(SettingMap settings, String key, int defaultValue1, int defaultValue2) {
    int value = (settings == null) ? defaultValue1 : settings.getInt32(key, defaultValue1);
    if (value <= 0) {
      value = defaultValue2;
    }
    return value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  public int getMaxPendingAcquires() {

    return maxPendingAcquires;
  }

  public void setMaxPendingAcquires(int maxPendingAcquires) {
    this.maxPendingAcquires = maxPendingAcquires;
  }

  public int getAcquireTimeout() {
    return acquireTimeout;
  }

  public void setAcquireTimeout(int acquireTimeout) {
    this.acquireTimeout = acquireTimeout;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public void setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
  }

  public int getWriteTimeout() {
    return writeTimeout;
  }

  public void setWriteTimeout(int writeTimeout) {
    this.writeTimeout = writeTimeout;
  }

  public int getWorkThreads() {
    return workThreads;
  }

  public void setWorkThreads(int workThreads) {
    this.workThreads = workThreads;
  }

  public InetSocketAddress getAddress() {
    return address;
  }

  public void setAddress(InetSocketAddress address) {
    this.address = address;
  }

  public boolean isReuseAddress() {
    return reuseAddress;
  }

  public void setReuseAddress(boolean reuseAddress) {
    this.reuseAddress = reuseAddress;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public int getReceiveBufferSize() {
    return receiveBufferSize;
  }

  public void setReceiveBufferSize(int receiveBufferSize) {
    this.receiveBufferSize = receiveBufferSize;
  }

  public int getSendBufferSize() {
    return sendBufferSize;
  }

  public void setSendBufferSize(int sendBufferSize) {
    this.sendBufferSize = sendBufferSize;
  }

  public boolean isTcpNoDelay() {
    return tcpNoDelay;
  }

  public void setTcpNoDelay(boolean tcpNoDelay) {
    this.tcpNoDelay = tcpNoDelay;
  }

  public boolean isKeepAlive() {
    return keepAlive;
  }

  public void setKeepAlive(boolean keepAlive) {
    this.keepAlive = keepAlive;
  }

  public int getKeepAliveTime() {
    return keepAliveTime;
  }

  public void setKeepAliveTime(int keepAliveTime) {
    this.keepAliveTime = keepAliveTime;
  }
}
