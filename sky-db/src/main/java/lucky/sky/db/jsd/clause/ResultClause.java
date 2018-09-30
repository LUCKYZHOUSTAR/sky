package lucky.sky.db.jsd.clause;

import lucky.sky.db.jsd.result.BuildResult;
import lucky.sky.db.jsd.result.CallResult;

/**
 * on 15/5/11.
 */
public interface ResultClause {

  CallResult result();

  BuildResult print();
}
