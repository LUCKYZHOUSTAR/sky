package lucky.sky.net.rpc;

import lucky.sky.net.rpc.annotation.RpcService;
import lucky.sky.util.lang.StrKit;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ServiceMeta {

  private String serviceName;
  private Map<String, String> methodNames = new HashMap<>();

  public ServiceMeta(Class<?> clazz) {
    RpcService rpcService = clazz.getAnnotation(RpcService.class);
    if (rpcService == null || StrKit.isBlank(rpcService.name())) {
      this.serviceName = clazz.getSimpleName();
    } else {
      this.serviceName = rpcService.name();
    }

    NamingConvention convention =
        rpcService == null ? NamingConvention.PASCAL : rpcService.convention();
    Method[] methods = clazz.getMethods();
    for (Method m : methods) {
      if (m.getDeclaringClass() == Object.class) {
        continue;
      }

      String methodName = ServiceContainer.getMethodName(m, convention);
      methodNames.put(m.getName(), methodName);
    }
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public Map<String, String> getMethodNames() {
    return methodNames;
  }

  public void setMethodNames(Map<String, String> methodNames) {
    this.methodNames = methodNames;
  }
}
