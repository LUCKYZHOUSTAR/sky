package lucky.sky.db.jsd.clause;

import lucky.sky.db.jsd.Table;

/**
 * on 15/5/21.
 */
public interface SelectClause {

  FromClause from(Table table);

  FromClause from(String table);
}
