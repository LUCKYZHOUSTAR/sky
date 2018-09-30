package lucky.sky.util.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.SocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class DefaultSocketClient {

  private static Logger logger = LoggerFactory.getLogger(DefaultSocketClient.class);
  public static final short MAGIC = (short) 0xbabe;
  public static final byte REQUEST = 0x01;     // Request
  private static int timeout = 10;

  public static byte[] sendReceive(String ip, int port, byte[] data) {
    Socket client = null;
    try {
      client = createConnection(ip, port);
      writeWithPreLen(client.getOutputStream(), data);
    } catch (Exception e) {
      logger.error("DefaultSocketClient sendReceive Exception, 发送失败", e);
      closeConnection(client);
      return null;
    }
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
      if (read(client.getInputStream(), bos) <= 0) {
        logger.error("DefaultSocketClient recv error, 接收异常");
        return null;
      }
      return bos.toByteArray();
    } catch (IOException e) {
      logger.error("DefaultSocketClient sendReceive Exception, 状态不明", e);
      return null;
    } finally {
      closeConnection(client);
    }
  }

  private static Socket createConnection(String host, int port) throws IOException {
    Socket socket = SocketFactory.getDefault().createSocket();
    socket.connect(new InetSocketAddress(host, port), timeout * 1000);
    socket.setSoTimeout(timeout * 1000);
    socket.setTcpNoDelay(true);
    return socket;
  }

  private static void closeConnection(Socket socket) {
    if (socket == null) {
      return;
    }
    try {
      socket.close();
    } catch (IOException e) {
      logger.error("DefaultSocketClient closeConnection IOException", e);
    }
  }

  private static void writeWithPreLen(OutputStream out, byte[] data) throws IOException {

    byte[] dataToSend = new byte[7 + data.length];
    ByteBuffer bb = ByteBuffer.wrap(dataToSend);
    bb.putShort(MAGIC);
    bb.put(REQUEST);
    bb.putInt(data.length);
    bb.put(data);
    bb.compact();
    out.write(bb.array());
    out.flush();
  }


  private static int read(InputStream in, OutputStream out) throws IOException {
    //read magic
    read(in, 2);
    read(in, 1);
    //读取报文的长度信息
    int length = readLen(in, 4);
    read(in, out, length);
    return length;
  }

  private static int read(InputStream in, OutputStream out, int preLen) throws IOException {
    if (preLen <= 0) {
      return 0;
    }

    int count = 0;
    byte[] buf = new byte[preLen];
    while (count < preLen) {
      int readlen = in.read(buf, 0, Math.min(preLen - count, 1024));
      if (readlen == -1) {
        break;
      }
      out.write(buf, 0, readlen);
      count += readlen;
    }

    if (count != preLen) {
      throw new IOException("data is not receive completed:(" + preLen + "," + count + ")");
    }
    return preLen;
  }


  private static void read(InputStream in, int prelen) throws IOException {
    if (prelen <= 0) {
      throw new IllegalArgumentException("request length not allow be < 0");
    }

    int count = 0;
    int off = 0;
    int c;
    while ((c = in.read()) != -1) {
      count++;
      if (count == prelen) {
        break;
      }
    }

    if (count != prelen) {
      throw new IllegalArgumentException("request length not equals");
    }
  }

  private static int readLen(InputStream in, int prelen) throws IOException {
    if (prelen <= 0) {
      return 0;
    }

    byte[] lendata = new byte[prelen];
    int count = 0;
    int off = 0;
    int c;
    while ((c = in.read()) != -1) {
      lendata[(off++)] = ((byte) c);
      count++;
      if (count == prelen) {
        break;
      }
    }

    if (count != prelen) {
      return 0;
    }

    int len = bytes2int(lendata);
    return len;
  }


  //高位在前，低位在后
  public static int bytes2int(byte[] bytes) {
    int result = 0;
    if (bytes.length == 4) {
      int a = (bytes[0] & 0xff) << 24;//说明二
      int b = (bytes[1] & 0xff) << 16;
      int c = (bytes[2] & 0xff) << 8;
      int d = (bytes[3] & 0xff);
      result = a | b | c | d;
    }
    return result;
  }
}