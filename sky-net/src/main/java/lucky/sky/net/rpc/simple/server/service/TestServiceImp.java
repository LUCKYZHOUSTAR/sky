package lucky.sky.net.rpc.simple.server.service;

/**
 *
 */
public class TestServiceImp implements TestService {

  @Override
  public void test() {
    // test do nothing
  }

  @Override
  public byte[] testByteArray(byte[] value) {
    return value;
  }

  @Override
  public boolean testBool(boolean value) {
    return value;
  }

  @Override
  public int testInt32(int value) {
    return value;
  }

  @Override
  public long testInt64(long value) {
    return value;
  }

  @Override
  public float testFloat(float value) {
    return value;
  }

  @Override
  public double testDouble(double value) {
    return value;
  }

  @Override
  public String testString(String value) {
    return value;
  }

  @Override
  public TestObject testProtobuf(TestObject value) {
    return value;
  }

  @Override
  public boolean[] testBoolArray(boolean[] value) {
    return value;
  }

  @Override
  public String[] testStringArray(String[] value) {
    return value;
  }

  @Override
  public Integer[] testInt32Array(Integer[] value) {
    return value;
  }

  @Override
  public long[] testInt64Array(long[] value) {
    return value;
  }

  @Override
  public float[] testFloatArray(float[] value) {
    return value;
  }

  @Override
  public double[] testDoubleArray(double[] value) {
    return value;
  }

  @Override
  public int testDivide(int n1, int n2) {
    return n1 / n2;
  }
}
