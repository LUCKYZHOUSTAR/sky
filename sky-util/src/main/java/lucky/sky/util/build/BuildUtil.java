package lucky.sky.util.build;

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

import java.nio.charset.StandardCharsets;

/**
 * 提供构建相关实用功能。
 */
public class BuildUtil {

  private static final Logger log = LoggerManager.getLogger(BuildUtil.class);

  public static String getBuildInfo() {
    try {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      return Resources.toString(cl.getResource("build.json"), StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error("failed to read build info", e);
      return "";
    }
  }

  public static String getBuildInfoSummary() {
    BuildInfo bi = getBuildInfoObject();
    if (bi == null) {
      return "";
    }
    return bi.toString();
  }

  public static BuildInfo getBuildInfoObject() {
    String bi = getBuildInfo();
    if (Strings.isNullOrEmpty(bi)) {
      return null;
    }
    return BuildInfo.ofJson(bi);
  }
}
