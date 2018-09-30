package lucky.sky.util.convert;

import java.text.DecimalFormat;

/**
 * 金额实用工具。
 */
public class MoneyConverter {

  /**
   * 100 分，即1元。
   */
  public static final int CentYuanBase = 100;

  /**
   * 元转换为分 HACK: float-number precision issue ?
   *
   * @param yuan 只支持精度到分的元，分之后将舍去，即小数点后两位
   */
  public static int yuanToCent(double yuan) {
    return (int) (yuan * CentYuanBase);
  }

  /**
   * 分转换为元，结果精确到小数据后两位
   */
  public static double centToYuan(double cent) {
    return cent / CentYuanBase;
  }

  public static String centToYuanAndString(double cent) {
    DecimalFormat df = new DecimalFormat("0.00");
    return df.format(cent / CentYuanBase);
  }

}
