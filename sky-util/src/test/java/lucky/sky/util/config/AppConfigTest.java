package lucky.sky.util.config;

import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * on 15/4/28.
 */
public class AppConfigTest {

  private static final Logger logger = LoggerManager.getLogger(AppConfigTest.class);

  @Before
  public void init() {
    ConfigManager.setConfigDir("src/main/resources/etc/");
  }

  @Test
  public void testReadSetting() {
    AppConfig cfg = AppConfig.getDefault();
    Assert.assertTrue(cfg != null);

    logger.info("app_name: {}", cfg.getAppName());
  }
}

