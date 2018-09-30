package lucky.sky.db.jsd.clause;

import lucky.sky.db.jsd.CallParams;


public interface CallClause extends CallEndClause {

  CallEndClause with(CallParams params);
}
