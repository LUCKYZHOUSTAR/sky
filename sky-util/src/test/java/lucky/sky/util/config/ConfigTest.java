package lucky.sky.util.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * on 15/5/20.
 */
public class ConfigTest {

  @Before
  public void init() {
    ConfigManager.setConfigDir("src/main/resources/etc/");
  }

  @Test
  public void testGetConfigPath() throws Exception {
    String path = ConfigManager.getConfigPath("db.redis.conf");
    println(path);
    Assert.assertTrue(path != "");
  }

  @Test
  public void testGlobalConfig() {
    println(AppConfig.getDefault().getGlobal().getZkAddress());
  }

  @Test
  public void testUrlConfig() {
    String url = UrlConfig.getDefault().getUrl("ShowTimeMovie", 38260);
    println(url);
  }

  private void println(String fmt, Object... args) {
    System.out.println(String.format(fmt, args));
  }
}
