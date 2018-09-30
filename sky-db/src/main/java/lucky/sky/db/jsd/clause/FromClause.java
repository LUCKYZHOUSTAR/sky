package lucky.sky.db.jsd.clause;

import lucky.sky.db.jsd.Filter;
import lucky.sky.db.jsd.Table;

/**
 * .
 */
public interface FromClause extends SelectEndClause {

  FromClause join(Table t, Filter on);

  FromClause join(String t, Filter on);

  FromClause leftJoin(Table t, Filter on);

  FromClause leftJoin(String t, Filter on);

  FromClause rightJoin(Table t, Filter on);

  FromClause rightJoin(String t, Filter on);

  FromClause fullJoin(Table t, Filter on);

  FromClause fullJoin(String t, Filter on);

  WhereClause where(Filter f);

  SelectEndClause limit(int skip, int take);

  SelectEndClause page(int pageIndex, int pageSize);
}

