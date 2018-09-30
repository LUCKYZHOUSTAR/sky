package lucky.sky.net.rpc.registry;

import java.util.Map;

/**
 *
 */
public class Provider {

  private String name;
  private String type;
  private String address;
  private String version;
  private String note;
  private String machine;
  private Map<String, String> settings;
  private int clients;

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

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getMachine() {
    return machine;
  }

  public void setMachine(String machine) {
    this.machine = machine;
  }

  public Map<String, String> getSettings() {
    return settings;
  }

  public void setSettings(Map<String, String> settings) {
    this.settings = settings;
  }

  public int getClients() {
    return clients;
  }

  public void setClients(int clients) {
    this.clients = clients;
  }
}
