package lucky.sky.util.etcd;


import lucky.sky.util.config.AppConfig;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.lang.UncheckedException;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @Author:chaoqiang.zhou
 * @Description:
 * @Date:Create in 18:11 2018/3/14
 */
public class ZkHolder {

  private CuratorFramework zkClient;
  private final int sessionTimeoutMs = 60 * 1000;
  private final int connectionTimeoutMs = 15 * 1000;
  private CountDownLatch connectedSemaphore = new CountDownLatch(1);
  private static final Logger logger = LoggerManager.getLogger(EtcdManager.class);


  public ZkHolder() {
    this.init();
  }

  public void init() {
    long start = System.currentTimeMillis();

    String etcdNodes = AppConfig.getDefault().getGlobal().getZkAddress();
    if (StrKit.isBlank(etcdNodes)) {
      throw new UncheckedException("找不到 etcd 配置信息");
    }
    try {
      zkClient = CuratorFrameworkFactory.builder().connectString(etcdNodes)
          .sessionTimeoutMs(sessionTimeoutMs).connectionTimeoutMs(connectionTimeoutMs)
          .retryPolicy(new ExponentialBackoffRetry(500, 20)).build();
      zkClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {
        @Override
        public void stateChanged(CuratorFramework curatorFramework,
            ConnectionState connectionState) {
          logger.info("Zookeeper connection state changed {}.", connectionState);
          if (connectionState == ConnectionState.LOST) {
            logger.info("Zookeeper connection lost,retry again");
            retry();
          } else if (connectionState == ConnectionState.CONNECTED) {
            logger.info("Zookeeper connection has benn established");
            connectedSemaphore.countDown();
          } else if (connectionState == ConnectionState.RECONNECTED) {
            logger.info(
                "Zookeeper connection has been re-established, will re-subscribe and re-register.");
          }
        }
      });

      zkClient.start();
      connectedSemaphore.await();
    } catch (Exception e) {
      logger.info("create zookeeper failed,error{}", e);
      throw new UncheckedException(e);
    }

    logger.debug("zookeeper client initialized.(took {} ms)", System.currentTimeMillis() - start);
  }

  public void retry() {
    try {
      //重连之前，先进行关闭之前的连接操作
      close();
      init();
    } catch (Exception e) {
      logger.error("reconnected zookeeper failed,error{}", e);
    }
  }


  public void close() throws IOException {
    try {
      this.zkClient.close();
      this.zkClient = null;
    } catch (Exception e) {
      logger.error("close zookeeper failed,error{}", e);
      throw e;
    }
  }


  public CuratorFramework getZkClient() {
    return zkClient;
  }

  /**
   * 获取默认客户端
   */
  public static CuratorFramework getDefaultClient() {
    return new ZkHolder().getZkClient();
  }
}
