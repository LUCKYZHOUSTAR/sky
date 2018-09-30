package lucky.sky.net.rpc.registry;

import lucky.sky.util.encode.JsonEncoder;
import lucky.sky.util.etcd.EtcdManager;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import lucky.sky.util.thread.Clock;
import mousio.etcd4j.responses.EtcdKeysResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

public class EtcdRegistry implements Registry {

  private static final Logger LOGGER = LoggerManager.getLogger(EtcdRegistry.class);
  private static final int DEFAULT_TTL_SECONDS = 60;  // 60 秒
  public static final EtcdRegistry INSTANCE = new EtcdRegistry();

  private int ttlSeconds;

  public EtcdRegistry() {
    this(DEFAULT_TTL_SECONDS);
  }

  public EtcdRegistry(int ttlSeconds) {
    this.ttlSeconds = ttlSeconds;
  }

  @Override
  public void register(Supplier<Provider> supplier) {
    Clock.set(() -> {
      Provider provider = supplier.get();
      if (provider == null) {
        LOGGER.error("register failed: supplier return null");
      } else {
        register(provider, this.ttlSeconds);
      }
    }, 0, ttlSeconds * 1000L);
  }

  @Override
  public void remove(Provider provider) {
    String key = getNodePath(provider.getName(), provider.getAddress());
    try {
      EtcdManager.deleteNode(key, false);
      LOGGER.info("remove provider [{} - {}] success", provider.getName(), provider.getAddress());
    } catch (Exception e) {
      LOGGER
          .error("remove provider [{} - {}] failed: {}", provider.getName(), provider.getAddress(),
              e);
    }
  }

  @Override
  public List<Provider> lookup(LookupInfo info) {
    // 查找节点
    List<Provider> providers = lookup(info.getRealName(), info.getGroup(), info.getVersion());
    // 订阅变更
    if (info.getConsumer() != null) {
      LookupTask task = new LookupTask(info);
      if (providers != null) {
        task.setProviders(providers);
      }
      Clock.set(task, ttlSeconds * 1000L, ttlSeconds * 1000L);
    }
    return providers;
  }

  private static void register(Provider provider, int ttl) {
    // 检查节点是否下线
    try {
      String key = getOfflineNodePath(provider);
      EtcdKeysResponse.EtcdNode node = EtcdManager.getNode(key);
      if (node != null) {
        LOGGER.info("register failed, node [{} - {}] is offline", provider.getName(),
            provider.getAddress());
        return;
      }
    } catch (Exception e) {
      LOGGER.error("register failed", e);
      return;
    }

    // 注册
    try {
      String key = getNodePath(provider.getName(), provider.getAddress());
      String value = JsonEncoder.DEFAULT.encode(provider);
      EtcdManager.setNode(key, value, ttl + 5);
      LOGGER.info("register success: {}", value);
    } catch (Exception e) {
      LOGGER.error("register failed", e);
    }
  }

  private static List<Provider> lookup(String name, String group, String version) {
    try {
      List<EtcdKeysResponse.EtcdNode> nodes = getServerNodes(name, group);
      if (nodes == null || nodes.isEmpty()) {
        LOGGER.debug("lookup providers for [name:{}, group:{}, version:{}] failed: nodes not found",
            name, group, version);
        return Collections.emptyList();
      }

      List<Provider> providers = new ArrayList<>();
      nodes.forEach(node -> {
        Provider provider = JsonEncoder.DEFAULT.decode(node.value, Provider.class);
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

  private static List<EtcdKeysResponse.EtcdNode> getServerNodes(String name, String group) {
    if (StrKit.isBlank(group)) {
      String dir = getNodesPath(name);
      return EtcdManager.getChildNodes(dir);
    }

    // 获取分组下的节点列表
    String dir = getGroupNodesPath(name, group);
    List<EtcdKeysResponse.EtcdNode> groupNodes = EtcdManager.getChildNodes(dir);
    if (groupNodes != null && !groupNodes.isEmpty()) {
      // 获取节点的详细信息
      List<EtcdKeysResponse.EtcdNode> nodes = new ArrayList<>();
      groupNodes.forEach(node -> {
        String address = EtcdManager.getNodeName(node);
        try {
          String path = getNodePath(name, address);
          EtcdKeysResponse.EtcdNode n = EtcdManager.getNode(path);
          if (n == null) {
            LOGGER
                .warn("can't find node for [name:{}, group:{}, address:{}]", name, group, address);
          } else {
            nodes.add(n);
          }
        } catch (Exception e) {
          LOGGER.warn("find group node for [name:{}, group:{}, address:{}] failed: {}", name, group,
              address, e);
        }
      });
      return nodes;
    }

    return Collections.emptyList();
  }

  // 分组节点目录
  private static String getGroupNodesPath(String name, String group) {
    return String.format("service/%s/clients/%s", name, group);
  }

  // 服务节点目录
  private static String getNodesPath(String name) {
    return String.format("/service/%s/providers", name);
  }

  // 服务节点路径
  private static String getNodePath(String name, String address) {
    return String.format("/service/%s/providers/%s", name, address);
  }

  // 下线节点路径
  private static String getOfflineNodePath(Provider provider) {
    return String.format("/service/%s/offlines/%s", provider.getName(), provider.getAddress());
  }

  static class LookupTask implements Runnable {

    LookupInfo info;
    // 上次获取的地址列表
    HashSet<String> addresses = new HashSet<>();

    public LookupTask(LookupInfo info) {
      this.info = info;
    }

    private void setProviders(List<Provider> providers) {
      this.addresses.clear();
      providers.forEach(p -> this.addresses.add(p.getAddress()));
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

    @Override
    public void run() {
      try {
        List<Provider> newProviders = lookup(info.getRealName(), info.getGroup(),
            info.getVersion());
        if (this.needRefresh(newProviders)) {
          info.getConsumer().accept(info.getName(), newProviders);
          setProviders(newProviders);
          LOGGER.info("providers of [{}] refreshed: {}", info.getName(),
              JsonEncoder.DEFAULT.encode(newProviders));
        }
      } catch (Exception e) {
        LOGGER.error("lookup providers of [{}] failed: {}", info.getName(), e);
      }
    }
  }

}
