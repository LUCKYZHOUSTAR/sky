package lucky.sky.util.convert;

import org.apache.commons.lang3.time.FastDateFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;



public final class DateConverter {

  private static DateTimeFormatter DEFAULT_NEW_FORMATTER = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final FastDateFormat DEFAULT_OLD_FORMATTER = FastDateFormat
      .getInstance("yyyy-MM-dd HH:mm:ss");

  /**
   * Epoch 1970/1/1 00:00:00.000 UTC 对应的 .NET DateTime.Ticks 。 DateTime.Ticks 属性 (System) :
   * https://msdn.microsoft.com/zh-cn/library/system.datetime.ticks.aspx
   */
  public static final long EPOCH_NET_TICKS = 621355968000000000L;
  // TODO: to check!!! hour = 8 ???
  public static final LocalDateTime EPOCH_DATE_TIME = LocalDateTime.of(1970, 1, 1, 8, 0, 0,
      0);//  ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()).toLocalDateTime();
  /**
   * UNIX 時間戳當天0點0分0秒
   */
  public static final LocalDate EPOCH_DAY = LocalDate.of(1970, 1, 1);

  /**
   * .NET DateTime.Ticks 转为 Unix 时间戳。
   */
  public static long netTicksToUnixTime(long ticks) {
    return (ticks - EPOCH_NET_TICKS) / 10000;
  }

  /**
   * Unix 时间戳转为 .NET DateTime.Ticks。
   */
  public static long unixTimeToNetTicks(long unixTime) {
    return unixTime * 10000 + EPOCH_NET_TICKS;
  }

  public static LocalDateTime netTicksToLocalDateTime(long ticks) {
    long ut = netTicksToUnixTime(ticks);
    return DateConverter.ofEpochMilli(ut);
  }

  public static long localDateTimeToNetTicks(LocalDateTime dateTime) {
    long ut = DateConverter.toEpochMilli(dateTime);
    return unixTimeToNetTicks(ut);
  }

  /**
   * 返回 Date.getTime 即 unix epoch，如 null，则返回 0。
   */
  public static long getTime(Date date) {
    return date == null ? 0 : date.getTime();
  }

  /**
   * 本地时间转换为 Unix 时间戳，距离 1970/1/1 00:00:00 UTC 的毫秒数
   */
  public static long toEpochMilli(LocalDateTime dt) {
    Objects.requireNonNull(dt, "arg dt");
    return dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  public static long toEpochMilli(LocalDate dt) {
    Objects.requireNonNull(dt, "arg dt");
    return dt.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  public static Date toDate(LocalDateTime dt) {
    Objects.requireNonNull(dt, "arg dt");
    return new Date(toEpochMilli(dt));
  }

  public static LocalDateTime toLocalDateTime(Date date) {
    Objects.requireNonNull(date, "arg date");
    return ofEpochMilli(date.getTime());
  }

  public static LocalDate toLocalDate(Date date) {
    return toLocalDateTime(date).toLocalDate();
  }

  public static LocalDate toLocalDate(long epochMilli) {
    return ofEpochMilli(epochMilli).toLocalDate();
  }

  /**
   * 将以毫秒为单位的 Unix 时间戳转为 LocalDateTime 对象。
   */
  public static LocalDateTime ofEpochMilli(long epochMilli) {
    Instant instant = Instant.ofEpochMilli(epochMilli);
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }

  /**
   * 时间格式化
   *
   * @param dt 时间
   * @param format 格式化信息, 如: yyyy-MM-dd HH:mm:ss
   */
  public static String toString(LocalDateTime dt, String format) {
    Objects.requireNonNull(dt, "arg dt");
    return dt.format(DateTimeFormatter.ofPattern(format));
  }

  /**
   * 时间格式化(yyyy-MM-dd HH:mm:ss)
   *
   * @param time 时间
   */
  public static String toString(LocalDateTime time) {
    return time.format(DEFAULT_NEW_FORMATTER);
  }

  /**
   * 时间格式化
   *
   * @param time 时间
   * @param format 格式化信息, 如: yyyy-MM-dd HH:mm:ss
   */
  public static String toString(Date time, String format) {
    return FastDateFormat.getInstance(format).format(time);
  }

  /**
   * 时间格式化(yyyy-MM-dd HH:mm:ss)
   *
   * @param time 时间
   */
  public static String toString(Date time) {
    return DEFAULT_OLD_FORMATTER.format(time);
  }

  /**
   * 时间格式化(yyyy-MM-dd HH:mm:ss)
   */
  public static String toString(long time) {
    return toString(DateConverter.ofEpochMilli(time));
  }

}
