package lucky.sky.db.jsd.template;

import lucky.sky.db.jsd.result.BuildResult;

/**
 * Created by Administrator on 2015/11/18.
 */
public interface SqlTemplate {

  BuildResult execute(Object obj);
}
