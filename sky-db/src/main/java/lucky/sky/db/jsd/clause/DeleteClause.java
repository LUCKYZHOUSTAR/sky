package lucky.sky.db.jsd.clause;

import lucky.sky.db.jsd.Filter;

public interface DeleteClause {

  DeleteEndClause where(Filter filter);
}
