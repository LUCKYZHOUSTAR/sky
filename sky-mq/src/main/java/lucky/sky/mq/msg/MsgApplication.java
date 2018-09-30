package lucky.sky.mq.msg;

import java.util.Map;
import lucky.sky.util.boot.Application;
import org.apache.commons.lang3.StringUtils;

/**
 * mq的服务端
 */
public class MsgApplication extends Application {

  private String consumeName;
  private MsgContainer msgContainer;

  public MsgApplication(Class<?> bootClass, String consumeName) {
    super(bootClass);
    this.consumeName = consumeName;
  }

  public MsgApplication(Class<?> bootClass, String[] args, String consumeName) {
    super(bootClass, args);
    this.consumeName = consumeName;
  }

  @Override
  protected void initDefaultProperties(Map<String, Object> props) {
    super.initDefaultProperties(props);
    //关闭springboot的控制台的提示信息
    props.put("spring.main.web_environment", false);
  }

  @Override
  protected void load() {
    scanHandlers();
  }

  private void scanHandlers() {

    if (StringUtils.isBlank(this.consumeName)) {
      logger.error("the consume name can not be null");
      throw new IllegalArgumentException("the consumeName can not be allowed null");
    }

    //设置消费者组，方便后台问题进心定位
    msgContainer = new MsgContainer(consumeName);
    Map<String, Handler> beans = ctx.getBeansOfType(Handler.class);
    logger.info("find {} handlers", beans.size());
    beans.forEach((k, handler) -> {
      Class<?> clazz = handler.getClass();
      MsgHandler msgHandler = clazz.getAnnotation(MsgHandler.class);
      if (msgHandler == null) {
        logger.warn("type [{}] isn't marked by @MsgHandler, skipping auto subscribe", clazz);
        return;
      }
      if (StringUtils.isBlank(msgHandler.tag()) || StringUtils.isBlank(msgHandler.topic())) {
        throw new IllegalArgumentException("can not allowed the topic is null and the tag is null");
      }
      //注册订阅的tag
      msgContainer.getSubscribeMap().put(msgHandler.topic(), msgHandler.tag());
      //放置订阅的handler
      msgContainer.registerHandler(handler, msgHandler.topic(), msgHandler.tag());
      logger.info("register handler:{}", clazz.getSimpleName());
    });

    //启动消费端的信息
    msgContainer.init();
  }

  public void setConsumeName(String consumeName) {
    this.consumeName = consumeName;
  }
}
