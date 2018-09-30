package lucky.sky.util.data;

import lucky.sky.util.lang.UncheckedException;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * on 15/12/22. 唯一 ID 生成器, 参考 MongoDB 驱动实现
 */
public final class Guid {

  private static final int LOW_ORDER_THREE_BYTES = 0x00ffffff;
  private static final int MACHINE_IDENTIFIER;
  private static final short PROCESS_IDENTIFIER;
  private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(
      (new SecureRandom()).nextInt());
  private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8',
      '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  static {
    try {
      MACHINE_IDENTIFIER = createMachineIdentifier();
      PROCESS_IDENTIFIER = createProcessIdentifier();
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
  }

  public static String get() {
    if ((MACHINE_IDENTIFIER & 0xff000000) != 0) {
      throw new IllegalArgumentException(
          "The machine identifier must be between 0 and 16777215 (it must fit in three bytes).");
    }

    int timestamp = dateToTimestampSeconds(new Date());
    int counter = NEXT_COUNTER.getAndIncrement() & LOW_ORDER_THREE_BYTES;

    char[] chars = new char[24];
    int i = 0;
    for (byte b : toByteArray(timestamp, MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, counter)) {
      chars[i++] = HEX_CHARS[b >> 4 & 0xF];
      chars[i++] = HEX_CHARS[b & 0xF];
    }
    return new String(chars);
  }

  private static byte[] toByteArray(int timestamp, int machineIdentifier, short processIdentifier,
      int counter) {
    byte[] bytes = new byte[12];
    bytes[0] = int3(timestamp);
    bytes[1] = int2(timestamp);
    bytes[2] = int1(timestamp);
    bytes[3] = int0(timestamp);
    bytes[4] = int2(machineIdentifier);
    bytes[5] = int1(machineIdentifier);
    bytes[6] = int0(machineIdentifier);
    bytes[7] = short1(processIdentifier);
    bytes[8] = short0(processIdentifier);
    bytes[9] = int2(counter);
    bytes[10] = int1(counter);
    bytes[11] = int0(counter);
    return bytes;
  }

  private static int createMachineIdentifier() {
    // build a 2-byte machine piece based on NICs info
    int machinePiece;
    try {
      StringBuilder sb = new StringBuilder();
      Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
      appendMacFingerprint(sb, e);
      machinePiece = sb.toString().hashCode();
    } catch (Exception t) {
      // exception sometimes happens with IBM JVM, use random
      machinePiece = new SecureRandom().nextInt();
    }
    machinePiece = machinePiece & LOW_ORDER_THREE_BYTES;
    return machinePiece;
  }

  private static void appendMacFingerprint(StringBuilder sb, Enumeration<NetworkInterface> e)
      throws SocketException {
    while (e.hasMoreElements()) {
      NetworkInterface ni = e.nextElement();
      sb.append(ni.toString());
      byte[] mac = ni.getHardwareAddress();
      if (mac != null) {
        ByteBuffer bb = ByteBuffer.wrap(mac);
        try {
          sb.append(bb.getChar());
          sb.append(bb.getChar());
          sb.append(bb.getChar());
        } catch (BufferUnderflowException shortHardwareAddressException) { //NOPMD
          // mac with less than 6 bytes. continue
        }
      }
    }
  }

  private static short createProcessIdentifier() {
    short processId;
    try {
      String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
      if (processName.contains("@")) {
        processId = (short) Integer.parseInt(processName.substring(0, processName.indexOf('@')));
      } else {
        processId = (short) java.lang.management.ManagementFactory.getRuntimeMXBean().getName()
            .hashCode();
      }

    } catch (Exception t) {
      processId = (short) new SecureRandom().nextInt();
    }

    return processId;
  }

  private static int dateToTimestampSeconds(final Date time) {
    return (int) (time.getTime() / 1000);
  }

  private static byte int3(int x) {
    return (byte) (x >> 24);
  }

  private static byte int2(int x) {
    return (byte) (x >> 16);
  }

  private static byte int1(int x) {
    return (byte) (x >> 8);
  }

  private static byte int0(int x) {
    return (byte) x;
  }

  private static byte short1(short x) {
    return (byte) (x >> 8);
  }

  private static byte short0(short x) {
    return (byte) x;
  }

}
