package lucky.sky.db.jsd.clause;

import lucky.sky.db.jsd.LockMode;
import lucky.sky.db.jsd.result.BuildResult;
import lucky.sky.db.jsd.result.SelectResult;

/**
 * on 15/5/27.
 */
public interface SelectEndClause {

  SelectResult result();

  SelectResult result(LockMode lockMode);

  BuildResult print();
}
