package lucky.sky.net.rpc;

import lucky.sky.net.rpc.simple.client.future.DefaultInvokeFuture;
import lucky.sky.net.rpc.simple.client.future.InvokeFuture;


@FunctionalInterface
public interface Invoker {

  InvokeFuture invoke(String service, String method, Object[] args, Class<?> returnType);
}
