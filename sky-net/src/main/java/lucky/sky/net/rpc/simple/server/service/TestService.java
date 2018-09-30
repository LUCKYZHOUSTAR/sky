package lucky.sky.net.rpc.simple.server.service;

import lucky.sky.net.rpc.annotation.RpcMethod;
import lucky.sky.net.rpc.annotation.RpcService;
import lucky.sky.util.lang.EnumValueSupport;

/**
 *
 */
@RpcService(name = "$test", description = "测试服务")
public interface TestService {

  @RpcMethod(description = "测试无参数")
  void test();

  @RpcMethod(description = "测试 byte[] 参数")
  byte[] testByteArray(byte[] value);

  @RpcMethod(description = "测试 string 参数")
  String testString(String value);

  @RpcMethod(description = "测试 bool 参数")
  boolean testBool(boolean value);

  @RpcMethod(description = "测试 int32 参数")
  int testInt32(int value);

  @RpcMethod(description = "测试 int64 参数")
  long testInt64(long value);

  @RpcMethod(description = "测试 float 参数")
  float testFloat(float value);

  @RpcMethod(description = "测试 double 参数")
  double testDouble(double value);

  @RpcMethod(description = "测试 protobuf 参数")
  TestObject testProtobuf(TestObject value);

  @RpcMethod(description = "测试 bool[] 参数")
  boolean[] testBoolArray(boolean[] value);

  @RpcMethod(description = "测试 string[] 参数")
  String[] testStringArray(String[] value);

  @RpcMethod(description = "测试 int32[] 参数")
  Integer[] testInt32Array(Integer[] value);

  @RpcMethod(description = "测试 int64[] 参数")
  long[] testInt64Array(long[] value);


  @RpcMethod(description = "测试 float[] 参数")
  float[] testFloatArray(float[] value);

  @RpcMethod(description = "测试 double[] 参数")
  double[] testDoubleArray(double[] value);

  @RpcMethod(description = "测试整数除法")
  int testDivide(int n1, int n2);

  class TestObject {

    public TestObjectType type;

    public String name;

    public String value;
  }

  enum TestObjectType implements EnumValueSupport {
    SIMPLE(1), COMPLEX(2);

    private final int value;

    TestObjectType(int value) {
      this.value = value;
    }

    @Override
    public int value() {
      return this.value;
    }
  }
}
