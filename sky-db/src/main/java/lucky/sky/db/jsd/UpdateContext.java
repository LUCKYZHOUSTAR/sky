package lucky.sky.db.jsd;

import lucky.sky.db.jsd.clause.UpdateClause;
import lucky.sky.db.jsd.result.BuildResult;
import lucky.sky.db.jsd.clause.SetClause;
import lucky.sky.db.jsd.clause.UpdateEndClause;
import lucky.sky.db.jsd.result.SimpleResult;

/**
 * 更新操作上下文 on 15/5/11.
 */
public class UpdateContext implements UpdateClause, SetClause, UpdateEndClause {

  private ConnectionManager manager;
  private Builder builder;
  private UpdateInfo info;

  UpdateContext(ConnectionManager manager, Builder builder, String table) {
    this.manager = manager;
    this.builder = builder;
    this.info = new UpdateInfo(table);
  }

  UpdateContext(ConnectionManager manager, Builder builder, Object obj, String... columns) {
    this(manager, builder, null, obj, columns);
  }

  UpdateContext(ConnectionManager manager, Builder builder, String table, Object obj,
      String... columns) {
    this.manager = manager;
    this.builder = builder;

    Mapper.EntityInfo entityInfo = Mapper.getEntityInfo(obj.getClass());
    this.info = new UpdateInfo(table == null ? entityInfo.table : table);

    UpdateValues values = new UpdateValues();
    String[] updateColumns =
        (columns == null || columns.length == 0) ? entityInfo.getUpdateColumns() : columns;
    for (String col : updateColumns) {
      values.add(col, entityInfo.getValue(obj, col));
    }
    this.info.values = values;

    BasicFilter filter = Filter.create();
    String[] idColumns = entityInfo.getIdColumns();
    for (String col : idColumns) {
      filter.add(col, entityInfo.getValue(obj, col));
    }
    this.info.where = filter;
  }

  @Override
  public SetClause set(UpdateValues values) {
    this.info.values = values;
    return this;
  }

  @Override
  public UpdateEndClause where(Filter filter) {
    this.info.where = filter;
    return this;
  }

  @Override
  public BuildResult print() {
    BuildResult result = this.builder.buildUpdate(this.info);
    return result;
  }

  @Override
  public SimpleResult result() {
    BuildResult result = this.builder.buildUpdate(this.info);
    Debug.log(result);
    return SimpleResult.of(manager, result);
  }

  static class UpdateInfo {

    String table;
    Filter where;
    UpdateValues values = new UpdateValues();

    UpdateInfo(String table) {
      this.table = table;
    }
  }
}
