package lucky.sky.db.jsd.clause;

import lucky.sky.db.jsd.result.BuildResult;
import lucky.sky.db.jsd.result.InsertResult;

/**
 * on 15/5/8.
 */
public interface InsertEndClause {

  BuildResult print();

  InsertResult result();

  InsertResult result(boolean returnKeys);
}
