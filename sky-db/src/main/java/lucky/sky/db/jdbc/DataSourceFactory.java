package lucky.sky.db.jdbc;

import lucky.sky.db.config.DatabaseConfig;
import lucky.sky.util.config.ConfigException;
import lucky.sky.util.lang.StrKit;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


public class DataSourceFactory {

  private static final Map<String, DataSource> dataSources = new HashMap<>();

  private DataSourceFactory() {
  }

  public synchronized static DataSource get(String name) {
    DataSource dataSource = dataSources.get(name);
    if (dataSource != null) {
      return dataSource;
    }

    DatabaseConfig.SqlDBInfo di = DatabaseConfig.get(name);
    dataSource = createDbcp2Source(di);
    dataSources.put(name, dataSource);
    return dataSource;
  }

  private static DataSource createDbcp2Source(DatabaseConfig.SqlDBInfo di) {
    BasicDataSource ds = new BasicDataSource();

    // set driver
    String driver = di.getDriver();
    if (driver == null || driver.isEmpty()) {
      switch (di.getType()) {
        case "mysql":
          driver = "com.mysql.jdbc.Driver";
          break;
//                case "mssql2005":
        case "mssql":
          driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
          break;
        default:
          throw new ConfigException("unknown provider type: " + di.getType());
      }
    }
    ds.setDriverClassName(driver);
    ds.setUrl(di.getUrl());

    // set auth
    if (!StrKit.isBlank(di.getUser())) {
      ds.setUsername(di.getUser());
    }
    if (!StrKit.isBlank(di.getPwd())) {
      ds.setPassword(di.getPwd());
    }

    // set options
    int maxTotal = di.getSettings().getInt32("MaxOpenConns", 100);
    int maxIdle = di.getSettings().getInt32("MaxIdleConns", 10);
    int acquireTimeout = di.getSettings().getInt32("AcquireTimeout", 10);
//        ds.setInitialSize(1); // 初始的连接数；
    ds.setMaxTotal(maxTotal);
//        ds.setMinIdle(minIdle);
    ds.setMaxIdle(maxIdle);
    ds.setMaxWaitMillis(acquireTimeout * 1000L);

    return ds;
  }

//    private static DataSource createBonecpSource(DatabaseConfig.DatabaseInfo di) {
//        BoneCPDataSource source = new BoneCPDataSource();
//        String driver = di.getDriver();
//        if (driver == null || driver.isEmpty()) {
//            switch (di.getProvider()) {
//                case "mysql":
//                    driver = "com.mysql.jdbc.Driver";
//                    break;
////                case "mssql2005":
//                case "mssql":
//                    driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//                    break;
//                default:
//                    throw new UncheckedException("unknown provider type: " + di.getProvider());
//            }
//        }
//        source.setDriverClass(driver);
//        source.setJdbcUrl(di.getAddress());
//
//        String username = di.getSettings().getString("Username");
//        String password = di.getSettings().getString("Password");
//        if (username != null && !username.isEmpty()) source.setUsername(username);
//        if (password != null && !password.isEmpty()) source.setPassword(password);
//        // source.setXXX(...);			// (other config options here)
//        return source;
//    }
}
