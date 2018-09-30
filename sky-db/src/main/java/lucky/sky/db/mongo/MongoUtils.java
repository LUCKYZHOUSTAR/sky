package lucky.sky.db.mongo;

import java.util.ArrayList;
import java.util.List;


public class MongoUtils {

  public static final String ID = "_id";
  public static final String SET = "$set";
  public static final String IN = "$in";
  public static final String INC = "$inc";
  public static final String NE = "$ne";
  public static final String PUSH = "$push";
  public static final String PULL = "$pull";
  public static final String GT = "$gt";
  public static final String OR = "$or";
  public static final String LT = "$lt";
  public static final String GTE = "$gte";
  public static final String LTE = "$lte";

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static List getMongoList(int[] ids) {
    List list = new ArrayList();
    for (int id : ids) {
      list.add(id);
    }
    return list;
  }
}
