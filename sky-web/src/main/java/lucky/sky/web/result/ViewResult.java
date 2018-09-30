package lucky.sky.web.result;

import org.springframework.web.servlet.ModelAndView;

/**
 * on 15/10/16.
 *
 * @deprecated 使用具体业务系统框架的 ViewResult，例如后台系统应该使用 mx.admin.portal.web.integration.ViewResult 。
 */
public class ViewResult extends ModelAndView {

  public ViewResult(String viewName) {
    super(viewName);
  }
}
