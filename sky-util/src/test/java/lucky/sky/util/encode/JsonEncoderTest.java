package lucky.sky.util.encode;

import com.alibaba.fastjson.annotation.JSONField;
import lucky.sky.util.lang.TypeWrapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * on 15/5/7.
 */
public class JsonEncoderTest {

  private JsonEncoder encoder;

  @Before
  public void init() {
    encoder = new JsonEncoder();
  }

  @Test
  public void testEncodeDecode() throws Exception {
    test(null, Object.class);
    test(123, int.class);
    test(false, boolean.class);
    testUser(new User(1, "noname"));

    List<User> users = new ArrayList<User>();
    users.add(new User(1, "noname"));
    testUsers(users);
  }

  private <T> void test(T obj, Class<T> clazz) throws Exception {
    String value = encoder.encode(obj);
    System.out.println(value);
    assertEquals(obj, encoder.decode(value, clazz));
  }

  private void testUser(User user) throws Exception {
    String value = encoder.encode(user);
    System.out.println(value);
    User u = encoder.decode(value, User.class);
    assertEquals(user.name, u.name);
  }

  private void testUsers(List<User> users) throws Exception {
    String value = encoder.encode(users);
    System.out.println(value);
    List<User> list = encoder.decode(value, new TypeWrapper<List<User>>() {
    });
    assertEquals(users.get(0).name, list.get(0).name);
  }

  @Getter
  @Setter
  static class User {

    @JSONField(name = "uid")
    private int id;
    private String name;

    public User() {

    }

    public User(int id, String name) {
      this.id = id;
      this.name = name;
    }
  }
}
