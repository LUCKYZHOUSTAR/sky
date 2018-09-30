package lucky.sky.db.jsd;

/**
 * on 15/11/19.
 */
public enum NameStyle {
  LOWER, UPPER, PASCAL, CAMEL;

  public static String transform(String name, NameStyle style) {
    switch (style) {
      case LOWER:
        return toLower(name);
      case UPPER:
        return toUpper(name);
      case PASCAL:
        return toPascal(name);
      case CAMEL:
        return toCamel(name);
      default:
        return name;
    }
  }

  private static String toCamel(String name) {
    StringBuilder sb = new StringBuilder();
    char lastChar = ' ';
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (i == 0) {
        sb.append(Character.toLowerCase(c));
      } else if (c != '_') {
        if (lastChar == '_') {
          sb.append(Character.toUpperCase(c));
        } else if (Character.isLowerCase(lastChar) && Character.isUpperCase(c)) {
          sb.append(c);
        } else {
          sb.append(Character.toLowerCase(c));
        }
      }
      lastChar = c;
    }
    return sb.toString();
  }

  private static String toPascal(String name) {
    StringBuilder sb = new StringBuilder();
    char lastChar = ' ';
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (i == 0) {
        sb.append(Character.toUpperCase(c));
      } else if (c != '_') {
        if (lastChar == '_') {
          sb.append(Character.toUpperCase(c));
        } else if (Character.isLowerCase(lastChar) && Character.isUpperCase(c)) {
          sb.append(c);
        } else {
          sb.append(Character.toLowerCase(c));
        }
      }
      lastChar = c;
    }
    return sb.toString();
  }

  private static String toUpper(String name) {
    StringBuilder sb = new StringBuilder();
    char lastChar = ' ';
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (Character.isUpperCase(c)) {
        if (Character.isLowerCase(lastChar)) {
          sb.append('_');
        }
        sb.append(c);
      } else {
        sb.append(Character.toUpperCase(c));
      }
      lastChar = c;
    }
    return sb.toString();
  }

  private static String toLower(String name) {
    StringBuilder sb = new StringBuilder();
    char lastChar = ' ';
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (Character.isLowerCase(c) || c == '_') {
        sb.append(c);
      } else if (Character.isUpperCase(c)) {
        if (Character.isLowerCase(lastChar)) {
          sb.append('_');
        }
        sb.append(Character.toLowerCase(c));
      }
      lastChar = c;
    }
    return sb.toString();
  }
}
