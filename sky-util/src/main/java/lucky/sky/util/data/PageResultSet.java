package lucky.sky.util.data;

import java.util.List;

/**
 * 定义分页结果集接口。
 */
public interface PageResultSet<T> {

  /**
   * 当前页数据列表
   */
  List<T> getItems();

  void setItems(List<T> items);

  /**
   * 总记录数
   */
  int getTotalCount();

  void setTotalCount(int totalCount);
}
