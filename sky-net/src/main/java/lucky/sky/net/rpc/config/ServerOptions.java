package lucky.sky.net.rpc.config;

import lucky.sky.util.config.AppConfig;
import lucky.sky.util.config.SettingMap;
import lucky.sky.util.lang.StrKit;

/**
 *
 */
public class ServerOptions {

  private String name;
  private String type = "simple";
  private String version;
  private boolean register;
  private String address;
  private String description;
  private SettingMap settings;

  public ServerOptions() {
    this.settings = new SettingMap();
    this.register = AppConfig.getDefault().getGlobal().isRpcRegisterEnabled();
  }

  public ServerOptions(String address, String description) {
    this.settings = new SettingMap();
    this.address = address;
    this.description = description;
    this.name = AppConfig.getDefault().getAppName();
    this.register = AppConfig.getDefault().getGlobal().isRpcRegisterEnabled();
  }

  public void cover(ServerOptions options) {
    if (options == null) {
      return;
    }

    if (!StrKit.isBlank(options.name)) {
      this.name = options.name;
    }
    if (!StrKit.isBlank(options.type)) {
      this.type = options.type;
    }
    if (!StrKit.isBlank(options.version)) {
      this.version = options.version;
    }
    this.register = options.register;
    if (!StrKit.isBlank(options.address)) {
      this.address = options.address;
    }
    if (!StrKit.isBlank(options.description)) {
      this.description = options.description;
    }
    options.settings.each(this.settings::put);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public boolean isRegister() {
    return register;
  }

  public void setRegister(boolean register) {
    this.register = register;
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
