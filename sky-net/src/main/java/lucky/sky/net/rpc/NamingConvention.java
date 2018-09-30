package lucky.sky.net.rpc;

import java.util.Objects;

/**
 *
 */
public enum NamingConvention {
  CAMEL("camel"), PASCAL("pascal");

  String name;

  NamingConvention(String name) {
    this.name = name;
  }

  public static NamingConvention of(String name) {
    Objects.requireNonNull(name, "name");
    switch (name.toLowerCase()) {
      case "camel":
        return CAMEL;
      case "pascal":
        return PASCAL;
      default:
        throw new IllegalArgumentException("Undefined NameStyle: " + name);
    }
  }

  public static String transform(String name, NamingConvention convention) {
    Character firstLetter = name.charAt(0);
    if (convention == NamingConvention.PASCAL) {
      firstLetter = Character.toUpperCase(firstLetter);
    } else {
      firstLetter = Character.toLowerCase(firstLetter);
    }
    return firstLetter.toString() + name.substring(1);
  }
}
