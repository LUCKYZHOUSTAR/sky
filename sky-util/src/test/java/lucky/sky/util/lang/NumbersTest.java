package lucky.sky.util.lang;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by xfwang on 2016/2/4.
 */
public class NumbersTest {

  @Test
  public void equalsZero() {
    Assert.assertTrue(Numbers.equalsZero(0)); // Integer
    Assert.assertTrue(Numbers.equalsZero(0L));
    Assert.assertFalse(Numbers.equalsZero(1)); // Integer
    Assert.assertFalse(Numbers.equalsZero(1L));
    Assert.assertTrue(Numbers.equalsZero(0.0f)); // Float
    Assert.assertTrue(Numbers.equalsZero(0.0));
    Assert.assertFalse(Numbers.equalsZero(0.12f)); // Float
    Assert.assertFalse(Numbers.equalsZero(0.12));
    Assert.assertFalse(Numbers.equalsZero(1.23f)); // Float
    Assert.assertFalse(Numbers.equalsZero(1.23));

    Assert.assertFalse(Numbers.equalsZero(-1)); // Integer
    Assert.assertFalse(Numbers.equalsZero(-1L));
    Assert.assertFalse(Numbers.equalsZero(-0.12f)); // Float
    Assert.assertFalse(Numbers.equalsZero(-0.12));
  }
}
