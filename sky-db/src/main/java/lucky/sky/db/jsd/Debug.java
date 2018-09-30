package lucky.sky.db.jsd;

import lucky.sky.db.jsd.result.BuildResult;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;


public final class Debug {

  private final static String LOGGER_NAME = "lucky.sky.db.jsd.debug";
  private static Logger LOGGER = LoggerManager.getLogger(LOGGER_NAME);
  private static boolean ENABLED = LoggerManager.hasLogger(LOGGER_NAME);

  static void log(BuildResult result) {
    if (ENABLED && LOGGER.isDebugEnabled()) {
      LOGGER.debug("sql: {}, args: {}", result.getSql(), result.getArgs());
    }
  }

  static void log(String sql, Object[] args) {
    if (ENABLED && LOGGER.isDebugEnabled()) {
      LOGGER.debug("sql: {}, args: {}", sql, args);
    }
  }
}
