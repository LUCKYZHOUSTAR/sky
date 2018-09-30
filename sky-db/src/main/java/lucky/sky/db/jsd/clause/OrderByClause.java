package lucky.sky.db.jsd.clause;

/**
 * .
 */
public interface OrderByClause extends SelectEndClause {

  SelectEndClause limit(int skip, int take);

  SelectEndClause page(int pageIndex, int pageSize);
}
