package lucky.sky.util.etcd;

import lucky.sky.util.lang.StrKit;
import lucky.sky.util.log.Logger;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.requests.EtcdKeyPutRequest;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.lang.UncheckedException;
import lucky.sky.util.log.LoggerManager;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


public final class EtcdManager {

  private static final Logger logger = LoggerManager.getLogger(EtcdManager.class);
  private static final int TIMEOUT_SECONDS = 5;
  private static final EtcdClient defaultClient;

  static {
    long start = System.currentTimeMillis();

    String etcdNodes = AppConfig.getDefault().getGlobal().getZkAddress();
    if (StrKit.isBlank(etcdNodes)) {
      throw new UncheckedException("找不到 etcd 配置信息");
    }

    String[] array = etcdNodes.split(",");
    URI[] uris = new URI[array.length];
    for (int i = 0; i < uris.length; i++) {
      uris[i] = URI.create(array[i]);
    }

    try {
      defaultClient = new EtcdClient(uris);
    } catch (Exception e) {
      throw new UncheckedException(e);
    }

    logger.debug("etcd client initialized.(took {} ms)", System.currentTimeMillis() - start);
  }

  /**
   * 获取默认客户端
   */
  public static EtcdClient getDefaultClient() {
    return defaultClient;
  }

  /**
   * 获取节点信息
   *
   * @param value 是否是目录
   * @param ttl 超时
   */
  public static void setNode(String key, String value, int ttl) {
    try {
      EtcdKeyPutRequest request = defaultClient.put(key, value)
          .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
      if (ttl > 0) {
        request.ttl(ttl);
      }
      request.send().get();
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
  }

  /**
   * 获取节点信息
   *
   * @param dir 是否是目录
   */
  public static void deleteNode(String key, boolean dir) {
    try {
      if (dir) {
        defaultClient.delete(key).send().get();
      } else {
        defaultClient.deleteDir(key).send().get();
      }
    } catch (EtcdException e) {
      if (e.errorCode == 100) {
        return;
      }
      throw new UncheckedException(e);
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
  }

  /**
   * 获取节点信息
   */
  public static EtcdKeysResponse.EtcdNode getNode(String key) {
    try {
      EtcdResponsePromise<EtcdKeysResponse> promise = defaultClient.get(key)
          .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS).send();
      EtcdKeysResponse response = promise.get();
      return response.node;
    } catch (EtcdException e) {
      if (e.errorCode == 100) {
        return null;
      }
      throw new UncheckedException(e);
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
  }

  /**
   * 获取子节点列表
   */
  public static List<EtcdKeysResponse.EtcdNode> getChildNodes(String key) {
    try {
      EtcdResponsePromise<EtcdKeysResponse> promise = defaultClient.getDir(key)
          .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS).send();
      EtcdKeysResponse response = promise.get();
      return response.node.nodes;
    } catch (EtcdException e) {
      if (e.errorCode == 100) {
        return Collections.emptyList();
      }
      throw new UncheckedException(e);
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
  }

  /**
   * 获取节点名字
   *
   * @param node 节点
   */
  public static String getNodeName(EtcdKeysResponse.EtcdNode node) {
    int index = node.key.lastIndexOf('/');
    return node.key.substring(index + 1);
  }
}
