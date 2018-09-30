package lucky.sky.net.rpc;


import lucky.sky.util.reflect.MethodAccess;

/**
 *
 */
public class MethodExecutor {

  private MethodAccess access;
  private int index;
  private Object object;

  public MethodExecutor(Object obj, MethodAccess access, int index) {
    this.object = obj;
    this.access = access;
    this.index = index;
  }

  public Object invoke(Object[] args) {
    return access.invoke(object, index, args);
  }

  public Class[] getParameterTypes() {
    return access.getParameterTypes()[index];
  }

//  public Class getReturnType() {
//    return access.getReturnTypes()[index];
//  }
}
