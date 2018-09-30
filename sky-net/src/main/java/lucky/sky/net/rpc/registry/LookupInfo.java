package lucky.sky.net.rpc.registry;

import lucky.sky.util.lang.StrKit;

import java.util.List;
import java.util.function.BiConsumer;


public final class LookupInfo {

  private String name;
  private String realName;
  private String group;
  private String version;
  private BiConsumer<String, List<Provider>> consumer;

  public LookupInfo(String name, String alias) {
    this.name = name;
    this.realName = StrKit.isBlank(alias) ? name : alias;
  }

  public String getName() {
    return name;
  }

  public String getRealName() {
    return realName;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public BiConsumer<String, List<Provider>> getConsumer() {
    return consumer;
  }

  public void setConsumer(BiConsumer<String, List<Provider>> consumer) {
    this.consumer = consumer;
  }
}
