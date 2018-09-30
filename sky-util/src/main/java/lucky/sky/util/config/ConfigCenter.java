package lucky.sky.util.config;

import lombok.Getter;
import lombok.Setter;
import lucky.sky.util.encode.JsonEncoder;
import lucky.sky.util.lang.TypeWrapper;
import lucky.sky.util.log.ConsoleLogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unchecked")
public final class ConfigCenter {

  /**
   * 获取共享配置
   */
  public static String getShare(String key) {
    RemoteLoader.Result result = RemoteLoader.load("/app/share?name=" + key);
    if (result.isSuccess()) {
      return JsonEncoder.DEFAULT.decode(result.getValue(), new TypeWrapper<Map<String, String>>() {
      }).get(key);
    }
    ConsoleLogger.info("config > share: load remote config %s failed: %s", key, result.getError());
    return null;
  }

  /**
   * 获取共享配置
   */
  public static Map<String, String> getShares(String... keys) {
    HashMap<String, String> args = new HashMap<>();
    String keyLine = String.join(",", keys);
    args.put("name", keyLine);
    RemoteLoader.Result result = RemoteLoader.load("/app/share", args);
    if (result.isSuccess()) {
      return JsonEncoder.DEFAULT.decode(result.getValue(), new TypeWrapper<Map<String, String>>() {
      });
    }
    ConsoleLogger
        .info("config > share: load remote config %s failed: %s", keyLine, result.getError());
    return Collections.EMPTY_MAP;
  }

  /**
   * 获取站点地址
   */
  public static String getSite(String name) {
    RemoteLoader.Result result = RemoteLoader.load("/app/server?name=" + name);
    if (result.isSuccess()) {
      List<UrlServer> servers = JsonEncoder.DEFAULT
          .decode(result.getValue(), new TypeWrapper<List<UrlServer>>() {
          });
      return servers.get(0).getUrl();
    }
    ConsoleLogger.info("config > site: load remote config %s failed: %s", name, result.getError());
    return null;
  }

  /**
   * 获取站点地址
   */
  public static Map<String, String> getSites(String... names) {
    HashMap<String, String> args = new HashMap<>();
    String keyLine = String.join(",", names);
    args.put("name", keyLine);
    RemoteLoader.Result result = RemoteLoader.load("/app/server", args);
    if (result.isSuccess()) {
      List<UrlServer> servers = JsonEncoder.DEFAULT
          .decode(result.getValue(), new TypeWrapper<List<UrlServer>>() {
          });
      Map<String, String> sites = new HashMap<>(servers.size());
      servers.forEach(s -> sites.put(s.getName(), s.getUrl()));
      return sites;
    }
    ConsoleLogger
        .info("config > site: load remote config %s failed: %s", keyLine, result.getError());
    return Collections.EMPTY_MAP;
  }


  @Getter
  @Setter
  private static class UrlServer {

    private String name;
    private String url;
  }
}
