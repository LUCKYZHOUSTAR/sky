package lucky.sky.net.rpc.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptyListSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import java.util.Collections;
import lucky.sky.net.rpc.serialization.Serializer;
import lucky.sky.net.rpc.serialization.kryo.io.Inputs;
import lucky.sky.net.rpc.serialization.kryo.io.Outputs;
import org.jupiter.common.concurrent.collection.ConcurrentSet;
import org.jupiter.common.util.internal.InternalThreadLocal;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * @Author:chaoqiang.zhou
 * @Date:Create in 上午11:52 2018/5/15
 */
public class KryoSerializer extends Serializer {

  private static ConcurrentSet<Class<?>> useJavaSerializerTypes = new ConcurrentSet<>();

  static {
    useJavaSerializerTypes.add(Throwable.class);
  }

  private static final InternalThreadLocal<Kryo> kryoThreadLocal = new InternalThreadLocal<Kryo>() {

    @Override
    protected Kryo initialValue() throws Exception {
      Kryo kryo = new Kryo();
      for (Class<?> type : useJavaSerializerTypes) {
        kryo.addDefaultSerializer(type, JavaSerializer.class);
      }
      kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
      kryo.setRegistrationRequired(false);
      kryo.setReferences(false);
      kryo.register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
      return kryo;
    }
  };

  public static void setJavaSerializer(Class<?> type) {
    useJavaSerializerTypes.add(type);
  }

  @Override
  public <T> byte[] writeObject(T obj) {
    Output output = Outputs.getOutput();
    Kryo kryo = kryoThreadLocal.get();
    try {
      kryo.writeObject(output, obj);
      return output.toBytes();
    } finally {
      Outputs.clearOutput(output);
    }
  }

  @Override
  public <T> T readObject(byte[] body, Class<T> clazz) {
    Input input = Inputs.getInput(body, 0, body.length);
    Kryo kryo = kryoThreadLocal.get();
    return kryo.readObject(input, clazz);
  }
}
