package lucky.sky.net.rpc;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lucky.sky.net.rpc.annotation.RpcMethod;
import lucky.sky.net.rpc.annotation.RpcParameter;
import lucky.sky.util.lang.StrKit;

/**
 * 服务信息
 */
@Getter
@Setter
public class ServiceInfo {

  // 名称
  private String name;

  // 描述
  private String description;

  // 方法列表
  private Map<String, MethodInfo> methods;

  public ServiceInfo(Class<?> clazz, String name, String description, NamingConvention convention) {
    this.name = name;
    this.description = description;
    this.methods = new HashMap<>();

    Method[] clazzMethods = clazz.getMethods();
    for (Method m : clazzMethods) {
      if (m.getDeclaringClass() == Object.class) {
        continue;
      }

      RpcMethod rpcMethod = m.getAnnotation(RpcMethod.class);
      MethodInfo mi = new MethodInfo();
      mi.name = ServiceContainer.getMethodName(m, convention);
      mi.description = rpcMethod == null ? null : rpcMethod.description();
      mi.returnType = getReturn(m);
      mi.parameters = getParameters(m);

      this.methods.put(mi.name, mi);
    }
  }
//
  private static List<ParameterInfo> getParameters(Method method) {
    Parameter[] parameters = method.getParameters();
    List<ParameterInfo> list = new ArrayList<>(parameters.length);
    if (parameters.length > 0) {
      for (Parameter p : parameters) {
        ParameterInfo pi = getParameter(p.getType(), p.getAnnotation(RpcParameter.class));
        if (StrKit.isBlank(pi.name)) {
          pi.name = p.getName();
        }
        list.add(pi);
      }
    }
    return list;
  }


  //todo:通过spring的方法来增加获取参数的实际名称
//  private static List<ParameterInfo> getParameters(Method method) {
//    LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
//    //获取参数的方法真实名称
//    String[] params = u.getParameterNames(method);
//    Parameter[] parameters = method.getParameters();
//    List<ParameterInfo> list = new ArrayList<>(parameters.length);
//    if (parameters.length > 0) {
//      int index = 0;
//      for (Parameter p : parameters) {
//        ParameterInfo pi = getParameter(p.getType(), p.getAnnotation(RpcParameter.class));
//        if (StrKit.isBlank(pi.name)) {
//          pi.name = params[index];
//        }
//        index = index + 1;
//        list.add(pi);
//      }
//    }
//    return list;
//  }

  private static ParameterInfo getReturn(Method method) {
    Class<?> returnType = method.getReturnType();
    if (returnType == null || returnType == void.class) {
      return null;
    }

    return getParameter(returnType, method.getAnnotation(RpcParameter.class));
  }

  private static ParameterInfo getParameter(Class<?> paramType, RpcParameter rpcParameter) {
    ParameterInfo pi = new ParameterInfo();
    pi.type = paramType;

    if (rpcParameter != null) {
      if (!StrKit.isBlank(rpcParameter.name())) {
        pi.name = rpcParameter.name();
      }
      pi.description = rpcParameter.description();
    }

    return pi;
  }

  // 方法信息
  @Getter
  @Setter
  public static class MethodInfo {

    // 名称
    private String name;

    // 描述
    private String description;

    // 参数列表
    private List<ParameterInfo> parameters;

    // 返回值
    private ParameterInfo returnType;
  }

  // 参数信息
  @Getter
  @Setter
  public static class ParameterInfo {

    // 名称
    private String name;

    // 类型
    private Class<?> type;

    // 描述
    private String description;
  }

}
