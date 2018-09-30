package lucky.sky.util.net;

import lucky.sky.util.log.LoggerManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * IP 实用工具。
 */
public class IPUtil {

  private static List<String> ips;

  private IPUtil() {
    // 防止实例化
  }

  /**
   * 返回本机地址。 说明： 仅返回 IPv4 不会返回回环地址 127.*。 如果多张网络卡，随机返回。
   */
  public static String getLocalIP() {
    safeLoadLocalIPList();
    return (ips.isEmpty()) ? "" : ips.get(0);
  }

  public static List<String> getLocalIPList() {
    safeLoadLocalIPList();
    return ips;
  }

  private static void safeLoadLocalIPList() {
    if (ips != null) {
      return;
    }

    synchronized (IPUtil.class) {
      if (ips != null) {
        return;
      }

      ips = loadLocalIPList();
    }
  }

  private static List<String> loadLocalIPList() {
    List<String> ips = new ArrayList<>(1);

    Enumeration<NetworkInterface> allNIC = null;
    try {
      allNIC = NetworkInterface.getNetworkInterfaces();
    } catch (Exception ex) {
      LoggerManager.getLogger(IPUtil.class).error("获取本机 ip 地址发生错误", ex);
    }

    while (allNIC != null && allNIC.hasMoreElements()) {
      NetworkInterface nic = allNIC.nextElement();
      Enumeration<InetAddress> addresses = nic.getInetAddresses();
      while (addresses.hasMoreElements()) {
        InetAddress ip = addresses.nextElement();
        if (ip != null && (ip instanceof Inet4Address) && (!ip.isAnyLocalAddress() && !ip
            .isLoopbackAddress())) {
          ips.add(ip.getHostAddress());
        }
      }
    }

    return ips;
  }

}
