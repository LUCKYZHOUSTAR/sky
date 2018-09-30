package lucky.sky.db.jsd.clause;

import lucky.sky.db.jsd.result.ExecuteResult;

/**
 * on 15/5/21.
 */
public interface ExecuteClause {

  ExecuteResult result();

  int submit();
}
