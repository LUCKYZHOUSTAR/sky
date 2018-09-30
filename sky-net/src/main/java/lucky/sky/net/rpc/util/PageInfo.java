package lucky.sky.net.rpc.util;

/**
 * 可用于 protobuffer 序列化的通用的分页信息对象。 功能与 PageInfo 一样，这里主要为了增加 pb 序列化需要的注解； 如果直接加在 PageInfo 上，lark-util
 * 与 lark-pb 将出现循环依赖。
 */
public class PageInfo extends lucky.sky.util.data.PageInfo {

  /**
   * 页码
   */
  private int pageIndex;

  /**
   * 每页大小
   */
  private int pageSize;

  /**
   * 是否计算总数量，默认 true
   *
   * @since 0.8.8
   */
  private boolean calculatesTotalCount = true;

  public PageInfo() {
    // for decode
  }

  public PageInfo(int limit) {
    super(limit);
  }

  public PageInfo(int pageIndex, int pageSize) {
    super(pageIndex, pageSize);
  }

  public PageInfo(int pageIndex, int pageSize, int pageBase, boolean calculatesTotalCount) {
    super(pageIndex, pageSize, pageBase, calculatesTotalCount);
  }

  public PageInfo(int pageIndex, int pageSize, boolean calculatesTotalCount) {
    super(pageIndex, pageSize, calculatesTotalCount);
  }

  public PageInfo(lucky.sky.util.data.PageInfo pageInfo) {
    super(pageInfo.getPageIndex(), pageInfo.getPageSize(), pageInfo.getPageBase(),
        pageInfo.isCalculatesTotalCount());
  }

  @Override
  public int getPageIndex() {
    return this.pageIndex;
  }

  @Override
  public void setPageIndex(int pageIndex) {
    this.pageIndex = pageIndex;
  }

  @Override
  public int getPageSize() {
    return this.pageSize;
  }

  @Override
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  @Override
  public boolean isCalculatesTotalCount() {
    return this.calculatesTotalCount;
  }

  @Override
  public void setCalculatesTotalCount(boolean calculatesTotalCount) {
    this.calculatesTotalCount = calculatesTotalCount;
  }
}
