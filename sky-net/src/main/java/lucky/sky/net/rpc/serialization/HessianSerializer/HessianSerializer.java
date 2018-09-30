package lucky.sky.net.rpc.serialization.HessianSerializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lucky.sky.net.rpc.serialization.HessianSerializer.io.Inputs;
import lucky.sky.net.rpc.serialization.HessianSerializer.io.Outputs;
import lucky.sky.net.rpc.serialization.Serializer;
import org.jupiter.common.util.ThrowUtil;
import org.jupiter.serialization.io.OutputStreams;

/**
 * @Author:chaoqiang.zhou
 * @Date:Create in 上午11:15 2018/5/15
 */
public class HessianSerializer extends Serializer {


  @Override
  public <T> T readObject(byte[] body, Class<T> clazz) {

    Hessian2Input input = Inputs.getInput(body, 0, body.length);
    try {
      Object obj = input.readObject(clazz);
      return clazz.cast(obj);
    } catch (IOException e) {
      ThrowUtil.throwException(e);
    } finally {
      try {
        input.close();
      } catch (IOException ignored) {}
    }
    return null; // never get here
  }

  @Override
  public <T> byte[] writeObject(T obj) {
    ByteArrayOutputStream buf = OutputStreams.getByteArrayOutputStream();
    Hessian2Output output = Outputs.getOutput(buf);
    try {
      output.writeObject(obj);
      output.flush();
      return buf.toByteArray();
    } catch (IOException e) {
      ThrowUtil.throwException(e);
    } finally {
      try {
        output.close();
      } catch (IOException ignored) {}

      OutputStreams.resetBuf(buf);
    }
    return null; // never get here
  }
}
