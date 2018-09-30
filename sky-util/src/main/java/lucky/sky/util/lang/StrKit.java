package lucky.sky.util.lang;

public class StrKit {

  /**
   * 空字符串。
   */
  public static final String empty = "";
  /**
   * 当前平台下回车换行符。
   */
  public static final String newLine = System.getProperty("line.separator");

  /**
   * 首字母变小写
   */
  public static String firstCharToLowerCase(String str) {
    char firstChar = str.charAt(0);
    if (firstChar >= 'A' && firstChar <= 'Z') {
      char[] arr = str.toCharArray();
      arr[0] += ('a' - 'A');
      return new String(arr);
    }
    return str;
  }

  /**
   * 首字母变大写
   */
  public static String firstCharToUpperCase(String str) {
    char firstChar = str.charAt(0);
    if (firstChar >= 'a' && firstChar <= 'z') {
      char[] arr = str.toCharArray();
      arr[0] -= ('a' - 'A');
      return new String(arr);
    }
    return str;
  }

  /**
   * 字符串为 null 或者为  "" 时返回 true
   */
  public static boolean isBlank(String str) {
    return str == null || str.isEmpty();
  }

  /**
   * 是否为 null 或 空字符串。
   */
  public static boolean isBlank(Object str) {
    return str == null || (str instanceof String && ((String) str).isEmpty());
  }

  /**
   * 字符串不为 null 而且不为  "" 时返回 true
   */
  public static boolean notBlank(String str) {
    return str != null && !str.isEmpty();
  }

  public static boolean notBlank(String... strings) {
    if (strings == null) {
      return false;
    }

    for (String str : strings) {
      if (str == null || str.isEmpty()) {
        return false;
      }
    }

    return true;
  }

  /**
   * 数组拼接成字符串
   */
  public static <T> String ArrayToString(Iterable<T> inputs, String separator) {
    if (inputs == null) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    for (T input : inputs) {
      if (sb.length() > 0) {
        sb.append(separator);
      }
      sb.append(input.toString());
    }
    return sb.toString();
  }
}




