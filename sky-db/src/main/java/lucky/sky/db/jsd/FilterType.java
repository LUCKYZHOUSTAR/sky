package lucky.sky.db.jsd;

/**
 * 条件类型
 */
public enum FilterType {
  EQ("="), NE("<>"), LT("<"), GT(">"), LTE("<="), GTE(">="), IN("IN"), LK("LIKE");

  String value;

  FilterType(String value) {
    this.value = value;
  }
}
