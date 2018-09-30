package lucky.sky.net.rpc;

import java.util.HashMap;
import java.util.Objects;
import lucky.sky.net.rpc.config.RpcServerConfig;
import lucky.sky.net.rpc.config.ServerOptions;
import lucky.sky.net.rpc.registry.Provider;
import lucky.sky.net.rpc.registry.Registry;
import lucky.sky.net.rpc.registry.ZkRegistry;
import lucky.sky.net.rpc.simple.server.SimpleServer;
import lucky.sky.net.rpc.simple.server.SimpleServerOptions;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.config.SettingMap;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

/**
 *
 */
public class RpcServer {

  private static final Logger logger = LoggerManager.getLogger(RpcServer.class);
  private static final Registry registry = ZkRegistry.INSTANCE;
  private ServerOptions options;
  private Server server;

  public RpcServer() {
    this(AppConfig.getDefault().appName());
  }

  public RpcServer(String name) {
    this(RpcServerConfig.get(name));
  }

  public RpcServer(ServerOptions options) {
    Objects.requireNonNull(options, "options can't be null");

    this.options = options;
    this.server = create(options);
  }

  public void registerService(Object instance) {
    server.registerService(instance);
  }

  public void registerService(Class<?> clazz, Object instance) {
    server.registerService(clazz, instance);
  }

  public void start() {
    this.server.start();
    if (options.isRegister()) {
      String address = getRegisterAddress();
      if (address == null) {
        return;
      }

      registry.register(() -> {
        Provider provider = new Provider();
        provider.setName(options.getName());
        provider.setType(options.getType());
        provider.setVersion(options.getVersion());
        provider.setNote(options.getDescription());
        provider.setClients((int) server.getData("clients.active"));
        provider.setAddress(address);

        SettingMap settings = options.getSettings();
        if (settings != null) {
          HashMap<String, String> providerSettings = new HashMap<>();
          settings.each(providerSettings::put);
          provider.setSettings(providerSettings);
        }
        return provider;
      });
    }
  }

  public ServerOptions getOptions() {
    return options;
  }

  public Object getData(String key) {
    return this.server.getData(key);
  }

  private String getRegisterAddress() {
    String address = options.getAddress();
    String[] parts = address.split(":");
    if (parts[0].isEmpty()) {
      String ip = AppConfig.getDefault().getGlobal().getRpcRegisterIP();
      if (StrKit.isBlank(ip)) {
        logger.error("无效的服务注册地址: {}, 请在 global.conf 文件中配置正确的 IP 地址", address);
        return null;
      }
      address = ip + address;
    }
    return address;
  }

  private static Server create(ServerOptions options) {
    if ("simple".equals(options.getType())) {
      return new SimpleServer(new SimpleServerOptions(options));
    }
    throw new RpcException(RpcError.SERVER_INVALID_SERVER_TYPE, options.getName(),
        options.getType());
  }
}
