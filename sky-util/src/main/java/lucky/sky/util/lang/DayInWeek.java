package lucky.sky.util.lang;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * 表示星期常量。 注意：正常情况下使用 Java 8 java.time.DayOfWeek 即可，提供此类型不是为了替代 DayOfWeek， 而是目前 lark-pb 不方便扩展任意枚举，但
 * lark-pb 已经支持了实现 EnumValueSupport 的枚举， 因此特定义此类型。
 */
public enum DayInWeek implements EnumValueSupport, EnumDisplayNameSupport {
  /**
   * The singleton instance for the day-of-week of Monday. This has the numeric value of {@code 1}.
   */
  MONDAY(1),
  /**
   * The singleton instance for the day-of-week of Tuesday. This has the numeric value of {@code
   * 2}.
   */
  TUESDAY(2),
  /**
   * The singleton instance for the day-of-week of Wednesday. This has the numeric value of {@code
   * 3}.
   */
  WEDNESDAY(3),
  /**
   * The singleton instance for the day-of-week of Thursday. This has the numeric value of {@code
   * 4}.
   */
  THURSDAY(4),
  /**
   * The singleton instance for the day-of-week of Friday. This has the numeric value of {@code 5}.
   */
  FRIDAY(5),
  /**
   * The singleton instance for the day-of-week of Saturday. This has the numeric value of {@code
   * 6}.
   */
  SATURDAY(6),
  /**
   * The singleton instance for the day-of-week of Sunday. This has the numeric value of {@code 7}.
   */
  SUNDAY(7);

  private int value;

  DayInWeek(int value) {
    this.value = value;
  }

  @Override
  public String displayName() {
    return DayOfWeek.of(this.value()).getDisplayName(TextStyle.FULL, Locale.SIMPLIFIED_CHINESE);
  }

  @Override
  public int value() {
    return this.value;
  }

  public DayOfWeek toDayOfWeek() {
    return DayOfWeek.of(this.value());
  }

  public static DayInWeek of(DayOfWeek dayOfWeek) {
    return Enums.valueOf(DayInWeek.class, dayOfWeek.getValue());
  }

  public static DayInWeek of(int value) {
    return Enums.valueOf(DayInWeek.class, value);
  }
}
