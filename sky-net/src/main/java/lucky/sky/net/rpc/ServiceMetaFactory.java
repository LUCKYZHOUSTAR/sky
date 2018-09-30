package lucky.sky.net.rpc;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class ServiceMetaFactory {

  private static final Map<Class<?>, ServiceMeta> metas = new HashMap<>();

  private ServiceMetaFactory() {
    // 防止实例化
  }

  public static synchronized ServiceMeta get(Class<?> clazz) {
    ServiceMeta meta = metas.get(clazz);
    if (meta == null) {
      meta = new ServiceMeta(clazz);
    }
    return meta;
  }
}
