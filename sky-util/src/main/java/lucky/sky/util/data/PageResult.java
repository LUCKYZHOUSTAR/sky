package lucky.sky.util.data;

import java.util.*;

/**
 * 分页结果
 *
 * @author xfwang
 */
public class PageResult<T> implements PageResultSet<T>, Collection<T> {

  /**
   * 当前页数据列表
   */
  private List<T> items;

  /**
   * 总记录数
   */
  private int totalCount;

  public PageResult() {
    // default ctor
  }

  public PageResult(List<T> items, int totalCount) {
    this.items = items;
    this.totalCount = totalCount;
  }

  @Override
  public List<T> getItems() {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    return this.items;
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
    if (totalCount < 0) {
      throw new IllegalArgumentException("totalCount can't be negative.");
    }
    this.totalCount = totalCount;
  }

  @Override
  public int size() {
    return this.items == null ? 0 : this.items.size();
  }

  @Override
  public boolean isEmpty() {
    return this.items == null ? true : this.items.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return this.items == null ? false : this.items.contains(o);
  }

  /**
   * Returns an iterator over elements of type {@code T}.
   *
   * @return an Iterator.
   */
  @Override
  public Iterator<T> iterator() {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    return this.items.iterator();
  }

  @Override
  public Object[] toArray() {
    return this.items == null ? new Object[0] : this.items.toArray();
  }

  @Override
  public <T1> T1[] toArray(T1[] a) {
    return this.items == null ? new ArrayList<T>().toArray(a) : this.items.toArray(a);
  }

  @Override
  public boolean add(T e) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    return this.items.add(e);
  }

  @Override
  public boolean remove(Object o) {
    if (this.items == null) {
      return false;
    }
    return this.items.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return this.items == null ? false : this.items.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    return this.items.addAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return this.items == null ? false : this.items.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return this.items == null ? false : this.items.retainAll(c);
  }

  @Override
  public void clear() {
    if (items != null) {
      this.items.clear();
    }
  }
}
