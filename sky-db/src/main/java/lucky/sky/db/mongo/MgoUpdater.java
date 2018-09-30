package lucky.sky.db.mongo;

import lucky.sky.util.lang.StrKit;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.Objects;

/**
 * UpdateOperations 的一个实现，扩展提供了一些实用方法。
 *
 */
public class MgoUpdater<T> implements UpdateOperations<T> {

  private UpdateOperations<T> upd;

  public MgoUpdater(UpdateOperations<T> updateOperations) {
    Objects.requireNonNull(updateOperations, "arg updateOperations");
    this.upd = updateOperations;
  }

  /**
   * 内部 UpdateOperations 对象
   */
  public UpdateOperations<T> updateOperations() {
    return this.upd;
  }

  /**
   * 如果 value == null 或者空字符串，则忽略不设置
   */
  public MgoUpdater setIfString(String fieldExpr, String value) {
    if (!StrKit.notBlank(value)) {
      this.upd.set(fieldExpr, value);
    }
    return this;
  }

  /**
   * 设置指定字段为新值 如果 value == null 则忽略不设置
   */
  public MgoUpdater setIf(String fieldExpr, Object value) {
    if (!ignore(value, null)) {
      this.upd.set(fieldExpr, value);
    }
    return this;
  }

  public MgoUpdater setIf(String fieldExpr, Object value, Object ignoreValue) {
    if (!ignore(value, ignoreValue)) {
      this.upd.set(fieldExpr, value);
    }
    return this;
  }

  /**
   * 如果 value == null 或者空字符串，则忽略不设置
   */
  public MgoUpdater setOnInsertIfString(String fieldExpr, String value) {
    if (!StrKit.notBlank(value)) {
      this.upd.setOnInsert(fieldExpr, value);
    }
    return this;
  }

  /**
   * sets the field on insert. 如果 value == null 则忽略不设置
   */
  public MgoUpdater setOnInsertIf(String fieldExpr, Object value) {
    if (!ignore(value, null)) {
      this.upd.setOnInsert(fieldExpr, value);
    }
    return this;
  }

  public MgoUpdater setOnInsertIf(String fieldExpr, Object value, Object ignoreValue) {
    if (!ignore(value, ignoreValue)) {
      this.upd.setOnInsert(fieldExpr, value);
    }
    return this;
  }

  /**
   * adds the value to an array field
   */
  public MgoUpdater addIf(String fieldExpr, Object value) {
    if (!ignore(value, null)) {
      this.upd.add(fieldExpr, value);
    }
    return this;
  }

  public MgoUpdater addIf(String fieldExpr, Object value, Object ignoreValue) {
    if (!ignore(value, ignoreValue)) {
      this.upd.add(fieldExpr, value);
    }
    return this;
  }

  public MgoUpdater addIf(String fieldExpr, Object value, boolean addDups) {
    if (!ignore(value, null)) {
      this.upd.add(fieldExpr, value, addDups);
    }
    return this;
  }

  public MgoUpdater addIf(String fieldExpr, Object value, boolean addDups, Object ignoreValue) {
    if (!ignore(value, ignoreValue)) {
      this.upd.add(fieldExpr, value, addDups);
    }
    return this;
  }

  private boolean ignore(Object value, Object ignoreValue) {
    if (ignoreValue == null) {
      return value == null;
    }
    return ignoreValue.equals(value);
  }

  /**
   * sets the field value
   */
  @Override
  public MgoUpdater<T> set(String fieldExpr, Object value) {
    this.upd.set(fieldExpr, value);
    return this;
  }

  /**
   * sets the field on insert.
   */
  @Override
  public MgoUpdater<T> setOnInsert(String fieldExpr, Object value) {
    this.upd.setOnInsert(fieldExpr, value);
    return this;
  }

  /**
   * removes the field
   */
  @Override
  public MgoUpdater<T> unset(String fieldExpr) {
    this.upd.unset(fieldExpr);
    return this;
  }

  /**
   * adds the value to an array field
   */
  @Override
  public MgoUpdater<T> add(String fieldExpr, Object value) {
    this.upd.add(fieldExpr, value);
    return this;
  }

  @Override
  public MgoUpdater<T> add(String fieldExpr, Object value, boolean addDups) {
    this.upd.add(fieldExpr, value, addDups);
    return this;
  }

  /**
   * adds the values to an array field
   */
  @Override
  public MgoUpdater<T> addAll(String fieldExpr, List<?> values, boolean addDups) {
    this.upd.addAll(fieldExpr, values, addDups);
    return this;
  }

  /**
   * removes the first value from the array
   */
  @Override
  public MgoUpdater<T> removeFirst(String fieldExpr) {
    this.upd.removeFirst(fieldExpr);
    return this;
  }

  /**
   * removes the last value from the array
   */
  @Override
  public MgoUpdater<T> removeLast(String fieldExpr) {
    this.upd.removeLast(fieldExpr);
    return this;
  }

  /**
   * removes the value from the array field
   */
  @Override
  public MgoUpdater<T> removeAll(String fieldExpr, Object value) {
    this.upd.removeAll(fieldExpr, value);
    return this;
  }

  /**
   * removes the values from the array field
   */
  @Override
  public MgoUpdater<T> removeAll(String fieldExpr, List<?> values) {
    this.upd.removeAll(fieldExpr, values);
    return this;
  }

  /**
   * decrements the numeric field by 1
   */
  @Override
  public MgoUpdater<T> dec(String fieldExpr) {
    this.upd.dec(fieldExpr);
    return this;
  }

  /**
   * increments the numeric field by 1
   */
  @Override
  public MgoUpdater<T> inc(String fieldExpr) {
    this.upd.inc(fieldExpr);
    return this;
  }

  /**
   * increments the numeric field by value (negatives are allowed)
   */
  @Override
  public MgoUpdater<T> inc(String fieldExpr, Number value) {
    this.upd.inc(fieldExpr, value);
    return this;
  }

  /**
   * sets the numeric field to value if it is greater than the current value.
   */
  @Override
  public MgoUpdater<T> max(String fieldExpr, Number value) {
    this.upd.max(fieldExpr, value);
    return this;
  }

  /**
   * sets the numeric field to value if it is less than the current value.
   */
  @Override
  public MgoUpdater<T> min(String fieldExpr, Number value) {
    this.upd.min(fieldExpr, value);
    return this;
  }

  /**
   * Turns on validation (for all calls made after); by default validation is on
   */
  @Override
  public MgoUpdater<T> enableValidation() {
    this.upd.enableValidation();
    return this;
  }

  /**
   * Turns off validation (for all calls made after)
   */
  @Override
  public MgoUpdater<T> disableValidation() {
    this.upd.disableValidation();
    return this;
  }

  /**
   * Enables isolation (so this update happens in one shot, without yielding)
   */
  @Override
  public MgoUpdater<T> isolated() {
    this.upd.isolated();
    return this;
  }
}
