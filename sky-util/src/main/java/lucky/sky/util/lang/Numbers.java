package lucky.sky.util.lang;

/**
 * 数字相关实用工具。
 *
 * @author xfwang
 */
public class Numbers {

  public static boolean equalsZero(Object num) {
    if (num instanceof Number) {
      double d = ((Number) num).doubleValue();
      return d >= 0 && d <= Double.MIN_VALUE;
    }
    return false;
  }
}
