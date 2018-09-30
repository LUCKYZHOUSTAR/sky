package lucky.sky.web.test.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author:chaoqiang.zhou
 * @Description:
 * @Date:Create in 10:54 2018/3/15
 */

@Getter
@Setter
public class ServiceInfo {


  private String name;
  private String type;
  private int port;
  private String developer;
  private String mark;
  private int nodes;
}
