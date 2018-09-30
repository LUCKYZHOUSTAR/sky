package lucky.sky.util.mx;

import lucky.sky.util.lang.StrKit;

import java.lang.management.ManagementFactory;

/**
 * utilities for management
 */
public class MXUtil {

  private static String pid;

  /**
   * 获取当前 JVM 进程ID。 当前实现仅支持 Hotspot
   */
  public static String getPID() {
    if (pid == null) {
      synchronized (MXUtil.class) {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        if (StrKit.notBlank(jvmName)) {
          String[] jvmNameParts = jvmName.split("@");
          if (jvmNameParts.length > 0) {
            pid = jvmNameParts[0];
          }
        }
        if (pid == null) {
          pid = "";
        }
      }
    }
    return pid;
  }
}
