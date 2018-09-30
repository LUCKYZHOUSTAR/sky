package lucky.sky.util.lang;

import lucky.sky.util.convert.MoneyConverter;
import org.junit.Assert;
import org.junit.Test;


public class MoneyConveterTest {

  @Test
  public void test_yuanToCent() {
    Assert.assertEquals(99, MoneyConverter.yuanToCent(0.99));
    Assert.assertEquals(199, MoneyConverter.yuanToCent(1.99));
    Assert.assertEquals(2001499900, MoneyConverter.yuanToCent(20014999));
  }

  @Test
  public void test_centToYuan() {
    Assert.assertEquals(0.99, MoneyConverter.centToYuan(99), 0);
    Assert.assertEquals(1.99, MoneyConverter.centToYuan(199), 0);
  }
}
