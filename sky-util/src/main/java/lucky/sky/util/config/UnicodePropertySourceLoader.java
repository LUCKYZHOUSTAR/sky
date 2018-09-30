package lucky.sky.util.config;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


public class UnicodePropertySourceLoader implements PropertySourceLoader {

  @Override
  public String[] getFileExtensions() {
    return new String[]{"properties"};
  }

  @Override
  public PropertySource<?> load(String name, Resource resource, String profile) throws IOException {
    if (profile == null) {
      Properties properties = new Properties();
      properties.load(new InputStreamReader(resource.getInputStream(), "UTF-8"));
      if (!properties.isEmpty()) {
        return new PropertiesPropertySource(name, properties);
      }
    }
    return null;
  }

}
