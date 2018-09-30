package lucky.sky.util.test;

import lucky.sky.util.test.spring.SpringJUnit;
import org.junit.BeforeClass;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public abstract class AbstractTest extends TestBase {

  @BeforeClass
  public static void init() {
//        System.setProperty("spring.config.name", "app.properties");
    //System.setProperty("spring.config.location", "classpath:/etc/");
    SpringJUnit.boot(Dummy.class, Dummy.class);
  }

  /**
   * HACK: LOOP ENDLESS IF ANNOTATING ON AbstractTest DIRECTLY
   */
  @SpringBootApplication
  public static class Dummy {

  }
}
