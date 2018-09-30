package lucky.sky.db.jsd;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 */
public final class Sorters {

  List<Sorter> list;

  public Sorters(SortType type, String... columns) {
    this.add(type, null, columns);
  }

  public Sorters(SortType type, Table table, String... columns) {
    this.add(type, table, columns);
  }

  public Sorters add(SortType type, String... columns) {
    return this.add(type, null, columns);
  }

  public Sorters add(SortType type, Table table, String... columns) {
    if (this.list == null) {
      this.list = new ArrayList<>();
    }
    Sorter sorter = new Sorter(type, table, columns);
    this.list.add(sorter);
    return this;
  }

  static class Sorter {

    SortType type;
    Table table;
    String[] columns;

    Sorter(SortType type, Table table, String[] columns) {
      this.type = type;
      this.table = table;
      this.columns = columns;
    }
  }
}
