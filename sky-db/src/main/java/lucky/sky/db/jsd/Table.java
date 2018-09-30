package lucky.sky.db.jsd;

/**
 * on 15/5/21.
 */
public abstract class Table {

  public abstract String getName();

  public abstract String getAlias();

  public abstract String getPrefix();

  public abstract Columns columns(String... cols);

  public abstract Groupers groupers(String... cols);

  public abstract Sorters sorters(SortType sortType, String... cols);

  public static Table create(String name) {
    return new SimpleTable(name, null);
  }

  public static Table create(String name, String alias) {
    return new SimpleTable(name, alias);
  }

  static class SimpleTable extends Table {

    private String name;
    private String alias;

    SimpleTable(String name, String alias) {
      this.name = name;
      this.alias = alias;
    }

    @Override
    public String getName() {
      return this.name;
    }

    @Override
    public String getAlias() {
      return this.alias;
    }

    @Override
    public String getPrefix() {
      if (this.alias == null || this.alias.length() == 0) {
        return name;
      }
      return this.alias;
    }

    @Override
    public Columns columns(String... cols) {
      Columns columns = new Columns();
      columns.add(this, cols);
      return columns;
    }

    @Override
    public Groupers groupers(String... cols) {
      return new Groupers(this, cols);
    }

    @Override
    public Sorters sorters(SortType sortType, String... cols) {
      return new Sorters(sortType, cols);
    }
  }

//    static class QueryTable extends Table {
//        private String alias;
//
//        @Override
//        public String getName() {
//            return this.alias;
//        }
//
//        @Override
//        public String getAlias() {
//            return this.alias;
//        }
//    }
}
