package lucky.sky.util.test;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lucky.sky.util.ioc.ServiceLocator;

import java.io.PrintStream;
import java.util.Objects;

/**
 * 测试基类，主要提供一些实用工具
 *
 */
public abstract class TestBase {

  /**
   * 输出流
   */
  @Getter
  private PrintStream out = System.out;

  public void print(Object obj) {
    out.print(obj);
  }

  public void println() {
    out.println("");
  }

  public void println(Object obj) {
    out.println(obj);
  }

  public void printJson(Object obj) {
    out.print(JSON.toJSONString(obj));
  }

  public void printlnJson(Object obj) {
    println();
    out.print(JSON.toJSONString(obj));
  }

  public void printJsonln(Object obj) {
    out.println(JSON.toJSONString(obj));
  }

  public void setOut(PrintStream out) {
    Objects.requireNonNull(out, "arg out");
    this.out = out;
  }

  public static <T> T getInstance(Class<T> type) {
    return ServiceLocator.current().getInstance(type);
  }
}
