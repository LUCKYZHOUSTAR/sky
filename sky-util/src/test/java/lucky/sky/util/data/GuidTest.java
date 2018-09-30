package lucky.sky.util.data;

import org.junit.Test;

/**
 * on 15/12/22.
 */
public class GuidTest {

  @Test
  public void testReadSetting() {
    String id = Guid.get();
    System.out.println(id);
  }
}
