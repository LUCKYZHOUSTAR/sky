package lucky.sky.db.jsd.clause;

import lucky.sky.db.jsd.UpdateValues;

/**
 * on 15/5/21.
 */
public interface UpdateClause {

  SetClause set(UpdateValues values);
}
