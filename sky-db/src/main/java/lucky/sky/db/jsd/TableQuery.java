package lucky.sky.db.jsd;

import lucky.sky.db.jsd.clause.*;

import java.util.List;

/**
 * on 16/3/31.
 */
public interface TableQuery {

  /**
   * 设置要插入记录的数据列
   *
   * @param columns 数据列
   */
  ColumnsClause columns(String... columns);

  /**
   * 插入操作
   *
   * @param obj 实体对象
   */
  InsertEndClause insert(Object obj);

  /**
   * 插入操作
   *
   * @param objects 实体对象列表
   */
  <T> InsertEndClause insert(List<T> objects);

  /**
   * 删除操作
   *
   * @param filter 条件
   */
  DeleteEndClause delete(Filter filter);

  /**
   * 删除操作
   *
   * @param obj 要删除的对象
   */
  DeleteEndClause delete(Object obj);

  /**
   * 更新操作
   *
   * @param values 要更新的数据
   */
  SetClause update(UpdateValues values);

  /**
   * 更新操作
   *
   * @param obj 实体对象
   */
  UpdateEndClause update(Object obj);

  /**
   * 更新操作
   *
   * @param obj 实体对象
   * @param columns 更新列
   */
  UpdateEndClause update(Object obj, String... columns);

  /**
   * 查询操作
   *
   * @param columns 查询返回列
   */
  FromClause select(String... columns);

  /**
   * 查询操作
   *
   * @param columns 查询返回列
   */
  FromClause select(Columns columns);

  /**
   * 查询操作
   *
   * @param clazz 查询映射对象类型
   */
  FromClause select(Class<?> clazz);

  class TableQueryImp implements TableQuery {

    private String name;
    private ConnectionManager manager;
    private Builder builder;

    TableQueryImp(String name, ConnectionManager manager, Builder builder) {
      this.name = name;
      this.manager = manager;
      this.builder = builder;
    }

    @Override
    public ColumnsClause columns(String... columns) {
      return new InsertContext(manager, this.builder, name).columns(columns);
    }

    @Override
    public InsertEndClause insert(Object obj) {
      return new InsertContext(manager, this.builder, obj, name);
    }

    @Override
    public <T> InsertEndClause insert(List<T> objects) {
      return new InsertContext(manager, this.builder, objects, name);
    }

    @Override
    public DeleteEndClause delete(Filter filter) {
      return new DeleteContext(manager, builder, name).where(filter);
    }

    @Override
    public DeleteEndClause delete(Object obj) {
      return new DeleteContext(manager, builder, obj, name);
    }

    @Override
    public SetClause update(UpdateValues values) {
      return new UpdateContext(manager, builder, name).set(values);
    }

    @Override
    public UpdateEndClause update(Object obj) {
      return new UpdateContext(manager, builder, name, obj);
    }

    @Override
    public UpdateEndClause update(Object obj, String... columns) {
      return new UpdateContext(manager, builder, name, obj, columns);
    }

    @Override
    public FromClause select(String... columns) {
      return new SelectContext(manager, builder, new Columns(null, columns)).from(name);
    }

    @Override
    public FromClause select(Columns columns) {
      return new SelectContext(manager, builder, columns).from(name);
    }

    @Override
    public FromClause select(Class<?> clazz) {
      return new SelectContext(manager, builder, clazz, name);
    }
  }
}
