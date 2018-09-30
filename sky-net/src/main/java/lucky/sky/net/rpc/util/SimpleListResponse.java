package lucky.sky.net.rpc.util;

import lucky.sky.util.data.PageResultSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 简单列表响应结果
 */
public class SimpleListResponse<T> implements PageResultSet<T> {

  private int totalCount;

  private List<T> items;

  @Override
  public List<T> getItems() {
    return items;
  }

  @Override
  public void setItems(List<T> items) {
    this.items = items;
  }

  @Override
  public int getTotalCount() {
    return totalCount;
  }

  @Override
  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }

  public void addItem(T item) {
    ensureItems();
    items.add(item);
  }

  public void addItems(Collection<? extends T> c) {
    ensureItems();
    items.addAll(c);
  }

  private void ensureItems() {
    if (items == null) {
      items = new ArrayList<>();
    }
  }
}
