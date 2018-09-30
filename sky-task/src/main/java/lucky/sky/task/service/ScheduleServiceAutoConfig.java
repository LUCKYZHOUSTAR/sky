package lucky.sky.task.service;

import lucky.sky.net.rpc.RpcClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @Author:chaoqiang.zhou
 * @Description:
 * @Date:Create in 18:20 2018/3/22
 */
@Configuration
@Lazy
@Order(Ordered.LOWEST_PRECEDENCE)
public class ScheduleServiceAutoConfig {

  private static final String SERVICE = "sky.task.center.service";

  /**
   * 订单服务
   */
  @Bean
  @ConditionalOnMissingBean
  public ScheduleService scheduleService() {
    return RpcClient.get(SERVICE, ScheduleService.class);
  }
}
