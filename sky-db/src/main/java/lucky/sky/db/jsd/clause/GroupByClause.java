package lucky.sky.db.jsd.clause;

import lucky.sky.db.jsd.Filter;
import lucky.sky.db.jsd.Sorters;

/**
 * .
 */
public interface GroupByClause extends SelectEndClause {

  SelectEndClause limit(int skip, int take);

  SelectEndClause page(int pageIndex, int pageSize);

  OrderByClause orderBy(Sorters sorters);

  HavingClause having(Filter f);
}
