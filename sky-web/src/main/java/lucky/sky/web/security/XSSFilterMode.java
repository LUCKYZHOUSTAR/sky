package lucky.sky.web.security;

/**
 * @Author:chaoqiang.zhou
 * @Date:Create in 下午6:59 2018/4/25 定义HTTP请求过滤模式
 */
public enum XSSFilterMode {

  /**
   * 如果有非法字符，则拒绝请求
   */
  REJECT,

  /**
   * 如果有非法字符，对非法字符进行转移
   */
  ESCAPE,
  /**
   * 清楚非法字符
   */
  CLEAN,
  /*
  不做任何的处理
   */
  NONE;
}
