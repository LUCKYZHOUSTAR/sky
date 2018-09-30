package lucky.sky.util.io;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * on 15/5/20.
 */
public class PathTest {
//    @Test
//    public void testJoin() {
//        String path = Path.join("/var/log/", "/abc", "all.log");
//        assertTrue(path.compareTo("/var/log/abc/all.log") == 0);
//    }

  @Test
  public void testGetExtension() {
    String ext = Path.getExtension("/var/log/all.log");
    assertTrue(ext.compareTo(".log") == 0);
  }
}
