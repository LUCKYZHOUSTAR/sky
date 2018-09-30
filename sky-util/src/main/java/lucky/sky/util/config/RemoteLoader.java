package lucky.sky.util.config;

import lombok.Getter;
import lucky.sky.util.encode.JsonEncoder;
import lucky.sky.util.etcd.EtcdManager;
import lucky.sky.util.log.Logger;
import mousio.etcd4j.responses.EtcdKeysResponse;
import lucky.sky.util.log.LoggerManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class RemoteLoader {

  private static final Logger logger = LoggerManager.getLogger(RemoteLoader.class);
  private static final String SERVER_NAME = "config-service";
  private static final int CONNECT_TIMEOUT = 5 * 1000;
  private static final int READ_TIMEOUT = 5 * 1000;
  private static List<String> providers;

  public static Result load(String path, Map<String, String> args) {
    StringBuilder sb = new StringBuilder(path);
    if (args != null && args.size() > 0) {
      int i = 0;
      Iterator<Map.Entry<String, String>> iterator = args.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<String, String> entry = iterator.next();
        if (entry.getValue() == null) {
          continue;
        }

        sb.append(i == 0 ? "?" : "&");
        sb.append(entry.getKey());
        sb.append("=");
        sb.append(encodeURL(entry.getValue()));
        i++;
      }
    }
    return load(sb.toString());
  }

  public static Result load(String path) {
    Result result = new Result();
    HttpURLConnection connection = null;
    String address = null;

    try {
      address = getAddress();
      URL url = new URL(address + path);
      connection = (HttpURLConnection) url.openConnection();
      connection.setUseCaches(false);
      connection.setConnectTimeout(CONNECT_TIMEOUT);
      connection.setReadTimeout(READ_TIMEOUT);

      result.value = readStream(connection.getInputStream());
      result.success = true;
    } catch (Exception e) {
      logger.error("load remote config failed, server: {}, path: {}, error: {}", address, path, e);

      if (connection == null) {
        result.error = e.getMessage();
      } else {
        try {
          result.error = readStream(connection.getErrorStream());
        } catch (Exception ex) {
          result.error = ex.getMessage();
        }
      }
    }

    return result;
  }

  private static String readStream(InputStream stream) throws IOException {
    if (stream == null) {
      return null;
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {

      int len;
      byte[] buffer = new byte[1024];
      while ((len = stream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, len);
      }
      return outputStream.toString(StandardCharsets.UTF_8.name());
    } finally {
      try {
        stream.close();
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }

      try {
        outputStream.close();
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }
  }

  private static String getAddress() {
    if (providers == null) {
      List<String> list = lookup();
      if (list.isEmpty()) {
        throw new ConfigException(String.format("can't find any providers for [%s]", SERVER_NAME));
      }
      providers = list;
    }

    int index = new Random().nextInt(providers.size());
    return providers.get(index);
  }

  // todo: refactor
  private static List<String> lookup() {
    List<String> providers = new ArrayList<>();

    try {
      String dir = getNodesPath(SERVER_NAME);
      List<EtcdKeysResponse.EtcdNode> nodes = EtcdManager.getChildNodes(dir);
      if (nodes != null) {
        nodes.forEach(n -> providers.add("http://" + EtcdManager.getNodeName(n)));
      }
      logger.info("lookup providers for [{}] success: {}", SERVER_NAME,
          JsonEncoder.DEFAULT.encode(providers));
    } catch (Exception e) {
      logger.error("lookup providers for [{}] failed: {}", SERVER_NAME, e);
    }

    return providers;
  }

  // 服务节点目录
  private static String getNodesPath(String name) {
    return String.format("/service/%s/providers", name);
  }

  private static String encodeURL(String value) {
    try {
      return URLEncoder.encode(value, "UTF-8");
    } catch (Exception e) {
      throw new ConfigException(e);
    }
  }

  @Getter
  public static class Result {

    private boolean success;
    private String error;
    private String value;

    public Result() {
      // default ctor
    }

    public Result(String error) {
      this.error = error;
    }
  }
}
