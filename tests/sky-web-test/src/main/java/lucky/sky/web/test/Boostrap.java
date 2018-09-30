

package lucky.sky.web.test;

import lucky.sky.web.WebApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Boostrap {

  public static void main(String[] args) throws Exception {
    new WebApplication(Boostrap.class, args).run();
  }

}
