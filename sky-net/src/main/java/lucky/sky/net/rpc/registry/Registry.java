package lucky.sky.net.rpc.registry;

import java.util.List;
import java.util.function.Supplier;

/**
 *
 */
public interface Registry {

  void register(Supplier<Provider> supplier);

  void remove(Provider provider);

  List<Provider> lookup(LookupInfo info);
}
