package lucky.sky.util.data;

/**
 * 带分页参数的查询参数
 */
public interface PageInfoSupport {

  default int getPageBase() {
    return 1;
  }

  default void setPageBase(int pageBase) {

  }

  int getPageIndex();

  void setPageIndex(int pageIndex);

  int getPageSize();

  void setPageSize(int pageSize);

  /**
   * 按 pageIndex & pageSize 计算出来的记录偏移量（从零开始）。
   */
  default int offset() {
    int pages = this.getPageIndex() - this.getPageBase();
    if (pages < 0) {
      pages = 0;
    }
    return pages * this.getPageSize();
  }

  default void copy(PageInfoSupport pageInfo) {
    this.setPageBase(pageInfo.getPageBase());
    this.setPageIndex(pageInfo.getPageIndex());
    this.setPageSize(pageInfo.getPageSize());
  }

  /**
   * 等同于 copy 方法。 此方法主要为兼容使用 setPageInfo 方法名的老代码名。
   */
  default void setPageInfo(PageInfoSupport pageInfo) {
    this.copy(pageInfo);
  }
}
