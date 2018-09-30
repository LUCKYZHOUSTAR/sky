package lucky.sky.net.rpc.config;

import lucky.sky.net.rpc.registry.Provider;
import lucky.sky.util.config.SettingMap;

import java.util.Objects;


public class ClientOptions {

  private String name;
  private String alias;
  private String type;
  private String group;
  private String version;
  private boolean discovery = true;
  private String address;
  private String description;
  private SettingMap settings;

  public ClientOptions() {
    settings = new SettingMap();
  }

  public ClientOptions(Provider provider) {
    Objects.requireNonNull(provider);

    this.name = provider.getName();
    this.type = provider.getType();
    this.version = provider.getVersion();
    this.address = provider.getAddress();
    this.description = provider.getNote();
    this.settings = (provider.getSettings() == null) ? new SettingMap()
        : new SettingMap(provider.getSettings());
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
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

  public boolean isDiscovery() {
    return discovery;
  }

  public void setDiscovery(boolean discovery) {
    this.discovery = discovery;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public SettingMap getSettings() {
    return settings;
  }

  public void setSettings(SettingMap settings) {
    this.settings = settings;
  }
}
