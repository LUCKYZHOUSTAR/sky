package lucky.sky.util.convert;


public class MethodNameConverter {

  private MethodNameConverter() {
    // 防止实例化
  }

  public static String toGetterName(String fieldName, boolean isBoolean) {
    return toAccessorName(fieldName, isBoolean, "is", "get");
  }

  public static String toSetterName(String fieldName, boolean isBoolean) {
    return toAccessorName(fieldName, isBoolean, "set", "set");
  }

  private static String toAccessorName(String fieldName, boolean isBoolean,
      String booleanPrefix, String normalPrefix) {
    if (fieldName.length() == 0) {
      return null;
    }

    if (isBoolean && fieldName.startsWith("is") &&
        fieldName.length() > 2 &&
        !Character.isLowerCase(fieldName.charAt(2))) {
      // The field is for example named 'isRunning'.
      return booleanPrefix + fieldName.substring(2);
    }
    return buildName(isBoolean ? booleanPrefix : normalPrefix, fieldName);
  }

  private static String buildName(String prefix, String suffix) {
    if (suffix.length() == 0) {
      return prefix;
    }
    if (prefix.length() == 0) {
      return suffix;
    }

    String realSuffix = suffix;
    char first = realSuffix.charAt(0);
    if (Character.isLowerCase(first)) {
      boolean useUpperCase = realSuffix.length() > 2 &&
          (Character.isTitleCase(realSuffix.charAt(1)) || Character
              .isUpperCase(realSuffix.charAt(1)));
      realSuffix = String.format("%s%s",
          useUpperCase ? Character.toUpperCase(first) : Character.toTitleCase(first),
          realSuffix.subSequence(1, realSuffix.length()));
    }
    return String.format("%s%s", prefix, realSuffix);
  }

}