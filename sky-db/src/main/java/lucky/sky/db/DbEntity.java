package lucky.sky.db;

/**
 * @author xfwang
 */
public interface DbEntity<K> {

  K getId();

  void setId(K id);
}
