package lucky.sky.db.jsd;

import lombok.Getter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * on 15/5/22.
 */
public class UpdateValues {

  private HashMap<String, UpdateValue> values = new HashMap<>();

  public UpdateValues() {
    // default ctor
  }

  public UpdateValues(String column, Object value) {
    this.add(column, value);
  }

  public UpdateValues add(String column, Object value) {
    return this.add(column, UpdateType.EQ, value);
  }

  /**
   * shortcut for add(column, INC, value)
   */
  public UpdateValues inc(String column, Object value) {
    return this.add(column, UpdateType.INC, value);
  }

  /**
   * shortcut for add(column, XP, value)
   */
  public UpdateValues xp(String column, Object value) {
    return this.add(column, UpdateType.XP, value);
  }

  public UpdateValues add(String column, UpdateType type, Object value) {
    this.values.put(column, new UpdateValue(type, value));
    return this;
  }

//    HashMap<String, UpdateValue> getValues() {
//        return values;
//    }

  public Iterator<Map.Entry<String, UpdateValue>> iterator() {
    return values.entrySet().iterator();
  }

  static class UpdateValue {

    @Getter
    private UpdateType type;
    @Getter
    private Object value;

    private UpdateValue(UpdateType type, Object value) {
      this.type = type;
      this.value = value;
    }
  }
}
