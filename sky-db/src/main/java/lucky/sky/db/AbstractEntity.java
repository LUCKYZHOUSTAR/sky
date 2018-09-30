package lucky.sky.db;

import lombok.Getter;
import lombok.Setter;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author xfwang
 */
@Getter
@Setter
@Entity(noClassnameStored = true)
public abstract class AbstractEntity<K> implements DbEntity<K> {

  @Id
  private K id;
}
