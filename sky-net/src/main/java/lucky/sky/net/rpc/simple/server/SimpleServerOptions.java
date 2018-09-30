package lucky.sky.net.rpc.simple.server;

import lucky.sky.net.rpc.config.ServerOptions;

import java.net.InetSocketAddress;

/**
 *
 */
public class SimpleServerOptions {

  private String version;
  private InetSocketAddress address;
  private int acceptThreads = Runtime.getRuntime().availableProcessors();
  private int workThreads = Runtime.getRuntime().availableProcessors() * 2;
  private int minThreads = 10;
  private int maxThreads = 500;
  private int maxClients = 1000;
  private int taskQueueSize;
  private int connectTimeout = 10 * 1000;
  private int readTimeout = 30 * 1000;
  private int writeTimeout = 30 * 1000;
  private int receiveBufferSize = 1024 * 64;
  private int sendBufferSize = 1024 * 64;
  private boolean keepAlive;
  private int keepAliveTime = 30 * 60;  // 单位秒, 默认 30 分钟
  private boolean tcpNoDelay;
  private int linger = 5;
  private boolean methodNameIgnoreCase;

  public SimpleServerOptions() {
    // default ctor
  }

  public SimpleServerOptions(ServerOptions options) {
    String[] parts = options.getAddress().split(":");
    this.address = new InetSocketAddress(Integer.parseInt(parts[1]));
    this.version = options.getVersion();
    this.minThreads = options.getSettings()
        .getInt32("MinThreads", Runtime.getRuntime().availableProcessors() << 1);
    this.maxThreads = options.getSettings().getInt32("MaxThreads", 512);
    this.taskQueueSize = options.getSettings().getInt32("TaskQueueSize");
    this.maxClients = options.getSettings().getInt32("MaxClients", 1000);
    this.connectTimeout = options.getSettings().getInt32("ConnectTimeout", 10 * 1000);
    this.readTimeout = options.getSettings().getInt32("ReadTimeout", 30 * 1000);
    this.writeTimeout = options.getSettings().getInt32("WriteTimeout", 30 * 1000);
    this.methodNameIgnoreCase = options.getSettings().getBool("MethodNameIgnoreCase", false);
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public InetSocketAddress getAddress() {
    return address;
  }

  public void setAddress(InetSocketAddress address) {
    this.address = address;
  }

  public int getAcceptThreads() {
    return acceptThreads;
  }

  public void setAcceptThreads(int acceptThreads) {
    this.acceptThreads = acceptThreads;
  }

  public int getWorkThreads() {
    return workThreads;
  }

  public void setWorkThreads(int workThreads) {
    this.workThreads = workThreads;
  }

  public int getMinThreads() {
    return minThreads;
  }

  public void setMinThreads(int minThreads) {
    this.minThreads = minThreads;
  }

  public int getMaxThreads() {
    return maxThreads;
  }

  public void setMaxThreads(int maxThreads) {
    this.maxThreads = maxThreads;
  }

  public int getMaxClients() {
    return maxClients;
  }

  public void setMaxClients(int maxClients) {
    this.maxClients = maxClients;
  }

  public int getTaskQueueSize() {
    return taskQueueSize;
  }

  public void setTaskQueueSize(int taskQueueSize) {
    this.taskQueueSize = taskQueueSize;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
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

  public boolean isTcpNoDelay() {
    return tcpNoDelay;
  }

  public void setTcpNoDelay(boolean tcpNoDelay) {
    this.tcpNoDelay = tcpNoDelay;
  }

  public int getLinger() {
    return linger;
  }

  public void setLinger(int linger) {
    this.linger = linger;
  }

  public boolean isMethodNameIgnoreCase() {
    return methodNameIgnoreCase;
  }
}
