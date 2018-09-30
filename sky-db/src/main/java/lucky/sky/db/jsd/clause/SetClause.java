package lucky.sky.db.jsd.clause;

import lucky.sky.db.jsd.Filter;

/**
 * on 15/5/22.
 */
public interface SetClause extends UpdateEndClause {

  UpdateEndClause where(Filter filter);
}
