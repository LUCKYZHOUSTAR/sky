package lucky.sky.net.rpc;


public interface Server {

  void registerService(Object instance);

  void registerService(Class<?> clazz, Object instance);

  void start();

  void stop();

  Object getData(String key);
}
