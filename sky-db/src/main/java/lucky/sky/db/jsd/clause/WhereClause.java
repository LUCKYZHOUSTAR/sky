package lucky.sky.db.jsd.clause;

import lucky.sky.db.jsd.Groupers;
import lucky.sky.db.jsd.Sorters;

/**
 * on 15/5/27.
 */
public interface WhereClause extends SelectEndClause {

  SelectEndClause limit(int skip, int take);

  SelectEndClause page(int pageIndex, int pageSize);

  GroupByClause groupBy(Groupers groupers);

  OrderByClause orderBy(Sorters sorters);
}
