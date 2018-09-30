package lucky.sky.mq.msg;

/**
 * @Author:chaoqiang.zhou
 * @Date:Create in 下午8:49 2018/5/7 https://blog.csdn.net/u014380653/article/details/52883356
 */
public enum MsgDelayLevel {

  //默认的延迟属性，已经够用
  //默认的延迟属性，已经够用
//1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h

  ONE_SECOND(1, "1s"),
  FIVE_SECOND(2, "5s"),
  TEN_SECOND(3, "10s"),
  THIRTY_SECOND(4, "30s"),
  ONE_MINUTE(5, "1m"),
  TWO_MINUTE(6, "2m"),
  THREE_MINUTE(7, "3m"),
  FOUR_MINUTE(8, "4m"),
  FIVE_MINUTE(9, "5m"),
  SIX_MINUTE(10, "6m"),
  SEVEN_MINUTE(11, "7m"),
  EIGHT_MINUTE(12, "8m"),
  NINE_MINUTE(13, "9m"),
  TEN_MINUTE(14, "10m"),
  TWEBTY_MINUTE(15, "20m"),
  THIRTY_MINUTE(16, "30m"),
  ONE_HOUR(17, "1h"),
  TWO_HOUR(18, "2h");

  private int level;
  private String name;


  MsgDelayLevel(int level, String name) {
    this.level = level;
    this.name = name;
  }


  public int level() {
    return this.level;
  }
}
