package lucky.sky.db.jsd.clause;

/**
 * on 15/5/13.
 */
public interface ValuesClause extends InsertEndClause {

  ValuesClause values(Object... values);
}
