package lucky.sky.net.rpc.registry;

import lucky.sky.util.encode.JsonEncoder;
import lucky.sky.util.etcd.ZkManager;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import lucky.sky.util.thread.Clock;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;


public class ZkRegistry implements Registry {

  private static final Logger LOGGER = LoggerManager.getLogger(ZkRegistry.class);
  private static final int DEFAULT_TTL_SECONDS = 60;  // 60 秒
  public static final ZkRegistry INSTANCE = new ZkRegistry();

  private int ttlSeconds;

  public ZkRegistry() {
    this(DEFAULT_TTL_SECONDS);
  }

  public ZkRegistry(int ttlSeconds) {
    this.ttlSeconds = ttlSeconds;
  }


  //IN CASE OF THE ZK DOWN,THE SERVICE CAN NOT BE REGISTER
  @Override
  public void register(Supplier<Provider> supplier) {
    Provider provider = supplier.get();
    if (provider == null) {
      LOGGER.error("register failed: supplier return null");
    } else {
      doRegister(provider);
    }
    Clock.set(() -> {
      if (provider == null) {
        LOGGER.error("register failed: supplier return null");
      } else {
        doRegister(provider);
      }
    }, DEFAULT_TTL_SECONDS * 1000);

  }


  /**
   * @param provider
   */
  public void doRegister(Provider provider) {
    try {
      //if offonline, then can not online again
      String key = getOfflineNodePath(provider);
      if (ZkManager.isPathExists(key)) {
        LOGGER.info("register failed, node [{} - {}] is offline", provider.getName(),
            provider.getAddress());
        return;
      }
    } catch (Exception e) {
      LOGGER.error("register failed", e);
      return;
    }
    try {
      String key = getNodePath(provider.getName(), provider.getAddress());
      String value = JsonEncoder.DEFAULT.encode(provider);
      //if existed,then update the node value.
      if (ZkManager.isPathExists(key)) {
        ZkManager.setNode(key, value);
        return;
      }
      // The znode will be deleted upon the client's disconnect.
      ZkManager.createPath(key, CreateMode.EPHEMERAL, value.getBytes(Charset.forName("utf-8")));
      LOGGER.info("register provider{} successed", JsonEncoder.DEFAULT.encode(provider));
    } catch (Exception e) {
      LOGGER
          .error("register provider:{} failed,the error:{},", JsonEncoder.DEFAULT.encode(provider),
              e);
    }
  }


  @Override
  public void remove(Provider provider) {
    String key = getNodePath(provider.getName(), provider.getAddress());
    try {
      ZkManager.deleteNode(key);
      LOGGER.info("remove provider [{} - {}] success", provider.getName(), provider.getAddress());
    } catch (Exception e) {
      LOGGER
          .error("remove provider [{} - {}] failed: {}", provider.getName(), provider.getAddress(),
              e);
    }
  }


  /**
   * 查询节点的时候注册一个watcher，节点有变更，更新本地的容器操作
   */
  @Override
  public List<Provider> lookup(LookupInfo info) {
    List<Provider> list = new ArrayList<>();
    // 查找节点
    list = lookup(info.getRealName(), info.getGroup(), info.getVersion());
    //注册监听事件
    doWatch(info);
    return list;
  }


  public void doWatch(LookupInfo info) {
    try {
      //订阅变更
      if (info.getConsumer() != null) {
        String serverPath = getNodesPath(info.getRealName());
        //观察该节点下面的，动态变更信息
        ZkManager.watcherChildrenList(serverPath, new NodeWatcher(info));
      }
    } catch (Exception e) {
      LOGGER.error("the server:{},watcher failed,the error{}", info.getRealName(), e);
    }

  }

  /**
   * 目前暂不支持分组的概念
   */
  private static List<Provider> lookup(String name, String group, String version) {
    try {
      List<String> nodes = getServerNodes(name);

      if (nodes == null || nodes.isEmpty()) {
        LOGGER.debug("lookup providers for [name:{}, group:{}, version:{}] failed: nodes not found",
            name, group, version);
        return Collections.emptyList();
      }

      List<Provider> providers = new ArrayList<>();
      nodes.forEach(node -> {
        String val = ZkManager.getNode(getServerPath(name, node));
        Provider provider = JsonEncoder.DEFAULT.decode(val, Provider.class);
        if (StrKit.isBlank(version) || provider.getVersion().equals(version)) {
          providers.add(provider);
        }
      });
      LOGGER.debug("lookup providers for [name:{}, group:{}, version:{}] success: {}", name, group,
          version, JsonEncoder.DEFAULT.encode(providers));
      return providers;
    } catch (Exception e) {
      LOGGER.error("lookup providers for [name:{}, group:{}, version:{}] failed: {}", name, group,
          version, e);
    }
    return Collections.emptyList();
  }

  /**
   * 暂不支持分组
   */
  private static List<String> getServerNodes(String name) {
    String dir = getNodesPath(name);
    return ZkManager.getChildNodes(dir);
  }

  // 分组节点目录
  private static String getGroupNodesPath(String name, String group) {
    return String.format("service/%s/clients/%s", name, group);
  }

  // 服务节点目录
  private static String getNodesPath(String name) {
    return String.format("/service/%s/providers", name);
  }

  private static String getServerPath(String name, String server) {
    return String.format("/service/%s/providers/%s", name, server);

  }

  // 服务节点路径
  private static String getNodePath(String name, String address) {
    return String.format("/service/%s/providers/%s", name, address);
  }

  // 下线节点路径
  private static String getOfflineNodePath(Provider provider) {
    return String.format("/service/%s/offlines/%s", provider.getName(), provider.getAddress());
  }

  static class NodeWatcher implements CuratorWatcher {

    // 上次获取的地址列表
    HashSet<String> addresses = new HashSet<>();
    private LookupInfo info;

    public NodeWatcher(LookupInfo info) {
      this.info = info;
    }

    private boolean needRefresh(List<Provider> providers) {
      if (providers == null || providers.isEmpty()) {
        return false;
      }

      if (providers.size() != addresses.size()) {
        return true;
      }

      for (Provider p : providers) {
        if (!this.addresses.contains(p.getAddress())) {
          return true;
        }
      }
      return false;
    }

    private void setProviders(List<Provider> providers) {
      this.addresses.clear();
      providers.forEach(p -> this.addresses.add(p.getAddress()));
    }


    public void doWatch(LookupInfo info) {
      try {
        //订阅变更
        if (info.getConsumer() != null) {
          String serverPath = getNodesPath(info.getRealName());
          //观察该节点下面的，动态变更信息
          ZkManager.watcherChildrenList(serverPath, new NodeWatcher(info));
        }
      } catch (Exception e) {
        LOGGER.error("the server:{},watcher failed,the error{}", info.getRealName(), e);
      }

    }

    @Override
    public void process(WatchedEvent event) throws Exception {
      LOGGER.info("server:{},node changed", event.getPath());

      if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
        LOGGER.info("server:{},node changed", event.getPath());
        try {
          List<Provider> newProviders = lookup(info.getRealName(), info.getGroup(),
              info.getVersion());
          if (this.needRefresh(newProviders)) {
            info.getConsumer().accept(info.getName(), newProviders);
            setProviders(newProviders);
            LOGGER.info("providers of [{}] refreshed: {}", info.getName(),
                JsonEncoder.DEFAULT.encode(newProviders));
          }
          //再次注册监听事件
          doWatch(this.info);
        } catch (Exception e) {
          LOGGER.error("lookup providers of [{}] failed", info.getName(), e);
        }
      }
    }
  }
}
