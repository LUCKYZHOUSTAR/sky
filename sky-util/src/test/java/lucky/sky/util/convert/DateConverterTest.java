package lucky.sky.util.convert;

import junit.framework.TestCase;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * Created by xfwang on 2015/10/16.
 */
public class DateConverterTest extends TestCase {

  public void test_netTicksToLocalDateTime() {
    LocalDateTime dt1 = DateConverter.netTicksToLocalDateTime(DateConverter.EPOCH_NET_TICKS);
    System.out.println(dt1);
    System.out.println(DateConverter.EPOCH_DATE_TIME);
    Assert.isTrue(dt1.isEqual(DateConverter.EPOCH_DATE_TIME));
  }

  public void test_netTicksToLocalDateTime2() {
    LocalDateTime dt = LocalDateTime.now();
    LocalDateTime dt1 = DateConverter
        .netTicksToLocalDateTime(DateConverter.localDateTimeToNetTicks(dt));
    System.out.println(dt1);
    System.out.println(dt);
    Assert.isTrue(dt1.isEqual(dt));
  }

  public void test_netTicksToLocalDateTime3() {
    LocalDateTime dt = LocalDateTime.now();
    LocalDateTime dt1 = DateConverter.netTicksToLocalDateTime(635806017007240000L);
    System.out.println(dt1);
    System.out.println(dt);
    Assert.isTrue(dt1.isEqual(dt));
  }

  public void test_localDateTimeToNetTicks() {
    LocalDateTime dt = DateConverter.EPOCH_DATE_TIME;
    long ticks = DateConverter.localDateTimeToNetTicks(dt);
    System.out.println(ticks);
    Assert.isTrue(ticks == DateConverter.EPOCH_NET_TICKS);
  }

  public void test_localDateTimeToNetTicks2() {
    LocalDateTime dt = LocalDateTime.of(2015, 10, 16, 1, 2, 3, 724);
    long ticks2 = 635806017000000000L;// 635806017007240000L;
    long ticks = DateConverter.localDateTimeToNetTicks(dt);
    System.out.println(DateConverter.netTicksToLocalDateTime(ticks));
    System.out.println(ticks);
    Assert.isTrue(ticks == ticks2);
  }
}
