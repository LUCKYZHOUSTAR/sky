package lucky.sky.db.jsd;

import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * on 16/6/13.
 */
public final class Releaser {

  private static Logger logger = LoggerManager.getLogger(Releaser.class);

  public static void release(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (Exception e) {
        logger.error("close rs failed", e);
      }
    }
  }

  public static void release(Statement statement) {
    if (statement != null) {
      try {
        statement.close();
      } catch (Exception e) {
        logger.error("close statement failed", e);
      }
    }
  }

  public static void release(ConnectionManager manager, Connection conn) {
    try {
      manager.closeConnection(conn);
    } catch (Exception e) {
      logger.error("close conn failed", e);
    }
  }
}
