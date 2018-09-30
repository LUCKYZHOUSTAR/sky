package lucky.sky.util.security.auth;

import lucky.sky.util.log.Logger;
import lucky.sky.util.context.InvokeContextHolder;
import lucky.sky.util.log.LoggerManager;

import java.security.Principal;
import java.util.Objects;

/**
 * 用户安全主体。
 */
public interface UserPrincipal extends Principal {

  class InnerHelper {

    public static final Logger log = LoggerManager.getLogger(UserPrincipal.class);
    //
    public static final String IC_KEY_CURRENT_PRINCIPAL = "mtime.lark.util.security.CurrentPrincipal";
  }

  public static void setPrincipal(UserPrincipal principal) {
    InvokeContextHolder.current().set(InnerHelper.IC_KEY_CURRENT_PRINCIPAL, principal);
  }

  /**
   * 获取当前安全主体。
   *
   * @param useAnonymous 如果没有当前上下文没有关联的安全主体，是否返回匿名用户主体。
   */
  public static UserPrincipal getPrincipal(boolean useAnonymous) {
    UserPrincipal p = getPrincipal();
    if (p == null && useAnonymous) {
      p = AnonymousUserPrincipal.getInstance();
    }
    return p;
  }

  /**
   * 获取当前安全主体。 如果没有当前上下文没有关联的安全主体，则返回 null。
   */
  public static UserPrincipal getPrincipal() {
    return InvokeContextHolder.current().get(InnerHelper.IC_KEY_CURRENT_PRINCIPAL);
  }

  /**
   * 获取当前安全主体。 如果没有当前上下文没有关联的安全主体，则返回抛出 IllegalStateException 。
   */
  public static UserPrincipal currentPrincipal() {
    UserPrincipal p = getPrincipal();
    if (p == null) {
      throw new IllegalStateException("not bound-UserPrincipal found");
    }
    return p;
  }

  /**
   * 获取关联的用户ID
   */
  int getUserId();

  /**
   * 获取关联的授权列表
   */
  String[] getAuthorities();

  /**
   * 身份是否已验证过。 缺省实现：如果 loginUserId 不为零则认为已验证。
   */
  public default boolean isAuthenticated() {
    return getUserId() != 0;
  }

  /**
   * 是否具有指定功能访问权限
   */
  public default boolean isAuthorized(String authority) {
    Objects.requireNonNull(authority, "arg authority");
    String[] authorities = getAuthorities();
    if (authorities != null && authorities.length > 0) {
      for (String r : authorities) {
        if (r.equalsIgnoreCase(authority)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 是否具有指定所有功能的访问权限，即 AND 关系。
   */
  public default boolean isAllAuthorized(String... authorities) {
    Objects.requireNonNull(authorities, "arg authorities");
    for (String f : authorities) {
      if (!isAuthorized(f)) {
        return false;
      }
    }
    return true;
  }

  /**
   * 是否具有指定功能列表任意访问权限，即 OR 关系。
   */
  public default boolean isAnyAuthorized(String... authorities) {
    Objects.requireNonNull(authorities, "arg authorities");
    for (String f : authorities) {
      if (isAuthorized(f)) {
        return true;
      }
    }
    return false;
  }

//    /**
//     * 判断当前用户是否具有指定 controller 和 action 的访问权限。
//     * 此方法作为 isAuthorized(String rightCode) 的快捷方式，适合一些常用的 MVC 结构，
//     * 内部使用 controller/action 作为 rightCode 参数传递。
//     *
//     * @param controller
//     * @param action
//     * @return
//     */
//    public default boolean isAuthorized(String controller, String action) {
//        String rightCode = controller + "/" + action;
//        UserPrincipal p = getPrincipal();
//        if (p == null) {
//            return false;
//        }
//        return p.isAuthorized(rightCode);
//    }
}
