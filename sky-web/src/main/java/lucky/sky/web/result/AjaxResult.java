package lucky.sky.web.result;

import lombok.Getter;
import lombok.Setter;

/**
 * on 15/10/16.
 */
@Getter
@Setter
public class AjaxResult {

  private boolean success;
  private Object value;
  private String error;
  //
}
