package lucky.sky.util.etcd;


import java.util.Collections;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.lang.UncheckedException;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author:chaoqiang.zhou
 * @Description:利用zk作为注册中心配置信息
 * @Date:Create in 17:33 2018/3/14
 */
public class ZkManager {

  private static final int sessionTimeoutMs = 60 * 1000;
  private static final int connectionTimeoutMs = 15 * 1000;
  private static CuratorFramework zkClient;
  private static final Logger logger = LoggerManager.getLogger(ZkManager.class);
  private static CountDownLatch latch = new CountDownLatch(1);

  /**
   * 获取节点信息
   *
   * @param value 是否是目录
   */
  public static void setNode(String key, String value) {
    try {
      zkClient.setData().forPath(key, value.getBytes(Charset.forName("utf-8")));
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
  }


  public static void createPath(String path, CreateMode mode, byte[] value) throws Exception {
    zkClient.create().creatingParentsIfNeeded().withMode(mode).forPath(path, value);
  }


  public static String watcherPath(String path, CuratorWatcher watcher)
      throws Exception {
    byte[] buffer = zkClient.getData().usingWatcher(watcher).forPath(path);
    return new String(buffer);
  }


  /**
   * 监听该节点列表的发生变更情况
   */
  public static List<String> watcherChildrenList(String path, CuratorWatcher watcher)
      throws Exception {
    return zkClient.getChildren().usingWatcher(watcher).forPath(path);
  }

  /**
   * 获取节点信息
   */
  public static void deleteNode(String key) {
    try {
      zkClient.delete().forPath(key);
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
  }

  /**
   * 获取节点信息
   */
  public static String getNode(String key) {
    try {
      byte[] buffer = zkClient.getData().forPath(key);
      return new String(buffer);
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
  }

  /**
   * 获取子节点列表
   */
  public static List<String> getChildNodes(String key) {
    try {

      if (!isPathExists(key)) {
        return Collections.EMPTY_LIST;
      }
      return zkClient.getChildren().forPath(key);
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
  }

  public static Stat getStat(String path) throws Exception {
    return (Stat) zkClient.checkExists().forPath(path);
  }

  public static boolean isPathExists(String path) throws Exception {
    Stat serverStat = getStat(path);
    if (serverStat == null) {
      return false;
    }
    return true;

  }


  public static void init() {
    long start = System.currentTimeMillis();
    String etcdNodes = AppConfig.getDefault().getGlobal().getZkAddress();
    if (StrKit.isBlank(etcdNodes)) {
      throw new UncheckedException("找不到 zookeeper 配置信息");
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
            latch.countDown();
          } else if (connectionState == ConnectionState.RECONNECTED) {
            logger.info(
                "Zookeeper connection has been re-established, will re-subscribe and re-register.");
          }
        }
      });

      zkClient.start();
      latch.await();
    } catch (Exception e) {
      logger.info("create zookeeper failed,error{}", e);
      throw new UncheckedException(e);
    }

    logger.debug("zookeeper client initialized.(took {} ms)", System.currentTimeMillis() - start);
  }

  public static void retry() {
    try {
      //重连之前，先进行关闭之前的连接操作
      close();
      latch = new CountDownLatch(1);
      init();
    } catch (Exception e) {
      logger.error("reconnected zookeeper failed,error{}", e);
    }
  }

  public static void close() throws IOException {
    try {
      zkClient.close();
      zkClient = null;
    } catch (Exception e) {
      logger.error("close zookeeper failed,error{}", e);
      throw e;
    }
  }
}
