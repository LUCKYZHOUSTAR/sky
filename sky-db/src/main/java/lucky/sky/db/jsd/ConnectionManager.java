package lucky.sky.db.jsd;

import java.sql.Connection;

/**
 * on 15/6/3.
 */
public interface ConnectionManager {

  Connection getConnection();

  void closeConnection(Connection conn);
}
