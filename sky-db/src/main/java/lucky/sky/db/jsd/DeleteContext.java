package lucky.sky.db.jsd;

import lucky.sky.db.jsd.clause.DeleteClause;
import lucky.sky.db.jsd.clause.DeleteEndClause;
import lucky.sky.db.jsd.result.BuildResult;
import lucky.sky.db.jsd.result.SimpleResult;

/**
 * 删除操作上下文 on 15/5/11.
 */
public class DeleteContext implements DeleteClause, DeleteEndClause {

  private ConnectionManager manager;
  private Builder builder;
  private DeleteInfo info;

  DeleteContext(ConnectionManager manager, Builder builder, String table) {
    this.manager = manager;
    this.builder = builder;
    this.info = new DeleteInfo(table);
  }

  DeleteContext(ConnectionManager manager, Builder builder, Object obj) {
    this(manager, builder, obj, null);
  }

  DeleteContext(ConnectionManager manager, Builder builder, Object obj, String table) {
    this.manager = manager;
    this.builder = builder;

    Mapper.EntityInfo entityInfo = Mapper.getEntityInfo(obj.getClass());
    String[] columns = entityInfo.getIdColumns();
    if (columns == null || columns.length == 0) {
      String error = String.format("类型 %s 没有带 Id 注解的属性", obj.getClass().getName());
      throw new JsdException(error);
    }

    BasicFilter f = Filter.create();
    for (String col : columns) {
      f.add(col, entityInfo.getValue(obj, col));
    }

    this.info = new DeleteInfo(table == null ? entityInfo.table : table);
    this.where(f);
  }

  @Override
  public DeleteEndClause where(Filter filter) {
    this.info.where = filter;
    return this;
  }

  @Override
  public SimpleResult result() {
    BuildResult result = this.builder.buildDelete(this.info);
    Debug.log(result);
    return SimpleResult.of(manager, result);
  }

  @Override
  public BuildResult print() {
    return this.builder.buildDelete(this.info);
  }

  static class DeleteInfo {

    String table;
    Filter where;

    public DeleteInfo(String table) {
      this.table = table;
    }
  }
}
