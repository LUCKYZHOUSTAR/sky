package lucky.sky.db.jsd;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 */
public final class Groupers {

  List<Grouper> items;

  public Groupers(String... columns) {
    this.add(null, columns);
  }

  public Groupers(Table table, String... columns) {
    this.add(table, columns);
  }

  public Groupers add(String... columns) {
    return this.add(null, columns);
  }

  public Groupers add(Table table, String... columns) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    Grouper grouper = new Grouper(table, columns);
    this.items.add(grouper);
    return this;
  }

  static class Grouper {

    Table table;
    String[] columns;

    Grouper(Table table, String[] columns) {
      this.table = table;
      this.columns = columns;
    }
  }
}
