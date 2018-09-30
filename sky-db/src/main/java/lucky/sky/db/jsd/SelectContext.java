package lucky.sky.db.jsd;

import lucky.sky.db.jsd.clause.*;
import lucky.sky.db.jsd.result.BuildResult;
import lucky.sky.db.jsd.result.SelectResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询操作上下文 on 15/5/11.
 */
public final class SelectContext implements SelectClause, FromClause, WhereClause, GroupByClause,
    OrderByClause, HavingClause {

  private ConnectionManager manager;
  private Builder builder;
  private SelectInfo info;

  SelectContext(ConnectionManager manager, Builder builder, Class<?> clazz) {
    this(manager, builder, clazz, null);
  }

  SelectContext(ConnectionManager manager, Builder builder, Class<?> clazz, String table) {
    Mapper.EntityInfo entityInfo = Mapper.getEntityInfo(clazz);
    Table t = Table.create(table == null ? entityInfo.table : table);
    Columns columns = new Columns(t, entityInfo.getUpdateColumns());
    if (entityInfo.getIdColumns() != null) {
      columns.add(t, entityInfo.getIdColumns());
    }

    this.manager = manager;
    this.builder = builder;
    this.info = new SelectInfo(columns.list, false);
    this.from(t);
  }

  SelectContext(ConnectionManager manager, Builder builder, Object obj) {
    Mapper.EntityInfo entityInfo = Mapper.getEntityInfo(obj.getClass());
    Table t = Table.create(entityInfo.table);
    BasicFilter f = Filter.create();
    Columns columns = new Columns(t, entityInfo.getUpdateColumns());
    if (entityInfo.getIdColumns() != null) {
      columns.add(t, entityInfo.getIdColumns());
      for (String c : entityInfo.getIdColumns()) {
        f.add(c, entityInfo.getValue(obj, c));
      }
    }

    this.manager = manager;
    this.builder = builder;
    this.info = new SelectInfo(columns.list, false);
    this.from(t);
    this.where(f);
  }

  SelectContext(ConnectionManager manager, Builder builder, Columns columns) {
    this.manager = manager;
    this.builder = builder;
    this.info = new SelectInfo(columns.list, columns.distinct);
  }

  @Override
  public FromClause from(Table table) {
    this.info.table = table;
    return this;
  }

  @Override
  public FromClause from(String table) {
    return this.from(new Table.SimpleTable(table, null));
  }

  @Override
  public FromClause join(Table t, Filter on) {
    Joiner joiner = new Joiner(JoinType.INNER, t, on);
    this.info.addJoiner(joiner);
    return this;
  }

  @Override
  public FromClause join(String t, Filter on) {
    return this.join(Table.create(t), on);
  }

  @Override
  public FromClause leftJoin(Table t, Filter on) {
    Joiner joiner = new Joiner(JoinType.LEFT, t, on);
    this.info.addJoiner(joiner);
    return this;
  }

  @Override
  public FromClause leftJoin(String t, Filter on) {
    return this.leftJoin(Table.create(t), on);
  }

  @Override
  public FromClause rightJoin(Table t, Filter on) {
    Joiner joiner = new Joiner(JoinType.RIGHT, t, on);
    this.info.addJoiner(joiner);
    return this;
  }

  @Override
  public FromClause rightJoin(String t, Filter on) {
    return this.rightJoin(Table.create(t), on);
  }

  @Override
  public FromClause fullJoin(Table t, Filter on) {
    Joiner joiner = new Joiner(JoinType.FULL, t, on);
    this.info.addJoiner(joiner);
    return this;
  }

  @Override
  public FromClause fullJoin(String t, Filter on) {
    return this.fullJoin(Table.create(t), on);
  }

  @Override
  public WhereClause where(Filter f) {
    this.info.where = f;
    return this;
  }

  @Override
  public SelectEndClause limit(int skip, int take) {
    this.info.skip = skip;
    this.info.take = take;
    return this;
  }

  @Override
  public SelectEndClause page(int pageIndex, int pageSize) {
    this.info.skip = (pageIndex - 1) * pageSize;
    this.info.take = pageSize;
    return this;
  }

  @Override
  public HavingClause having(Filter f) {
    this.info.having = f;
    return this;
  }

  @Override
  public GroupByClause groupBy(Groupers groupers) {
    if (groupers != null) {
      this.info.groups = groupers.items;
    }
    return this;
  }

  @Override
  public OrderByClause orderBy(Sorters sorters) {
    if (sorters != null) {
      this.info.orders = sorters.list;
    }
    return this;
  }

  @Override
  public SelectResult result() {
    BuildResult br = this.builder.buildSelect(this.info);
    Debug.log(br);
    return new SelectResult(manager, br.getSql(), br.getArgs());
  }

  @Override
  public SelectResult result(LockMode lockMode) {
    this.info.lock = lockMode;
    BuildResult br = this.builder.buildSelect(this.info);
    Debug.log(br);
    return new SelectResult(manager, br.getSql(), br.getArgs());
  }

  @Override
  public BuildResult print() {
    return this.builder.buildSelect(this.info);
  }

  static class SelectInfo {

    Table table;
    boolean distinct;
    LockMode lock = LockMode.NONE;
    List<Columns.Column> columns;
    List<Joiner> joins;
    Filter where;
    List<Groupers.Grouper> groups;
    Filter having;
    List<Sorters.Sorter> orders;
    int skip;
    int take;

    SelectInfo(List<Columns.Column> columns, boolean distinct) {
      this.columns = columns;
      this.distinct = distinct;
    }

    void addJoiner(Joiner joiner) {
      if (this.joins == null) {
        this.joins = new ArrayList<>();
      }
      this.joins.add(joiner);
    }
  }

  static class Joiner {

    JoinType type;
    Table table;
    Filter on;

    Joiner(JoinType type, Table table, Filter on) {
      this.type = type;
      this.table = table;
      this.on = on;
    }
  }

  enum JoinType {
    INNER("JOIN"), LEFT("LEFT JOIN"), RIGHT("RIGHT JOIN"), FULL("FULL JOIN");

    String value;

    JoinType(String value) {
      this.value = value;
    }
  }

}
