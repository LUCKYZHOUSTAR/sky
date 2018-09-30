package lucky.sky.util.security;

import lucky.sky.util.lang.UncheckedException;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;

/**
 * 提供加密解密辅助功能
 *
 */
public class EncryptUtil {

  private static final byte[] DFT_AES_KEY = {(byte) 0xf3, (byte) 0x91, (byte) 0xd6, (byte) 0xff,
      0x32, 0x1f, 0x4a, 0x02, (byte) 0xe4, (byte) 0x88, 0x25, (byte) 0x90, 0x72, (byte) 0xb3,
      (byte) 0xa7, 0x11};
  private static final byte[] DFT_AES_IV = {0x15, 0x33, 0x21, (byte) 0x9f, (byte) 0xb6, (byte) 0xdc,
      (byte) 0xaf, (byte) 0xd8, (byte) 0xd4, 0x37, 0x5f, (byte) 0x95, 0x13, (byte) 0xe4, 0x72,
      (byte) 0xdd};
  private static final int SALT_LENGTH = 16;
  private static final String ENCODING = "utf-8";
  private static final String AES_CIPHER = "AES/CBC/PKCS5Padding";

  public static String aesEncrypt(String plainText) {
    return aesEncrypt(plainText, DFT_AES_KEY);
  }

  public static String aesEncrypt(String plainText, String key) {
    Objects.requireNonNull(key, "arg key");
    return aesEncrypt(plainText, getBytes(key));
  }

  private static String aesEncrypt(String plainText, byte[] key) {
    Objects.requireNonNull(plainText, "arg plainText");
    Objects.requireNonNull(key, "arg key");
    try {
      Cipher cipher = Cipher.getInstance(AES_CIPHER);
      cipher.init(Cipher.ENCRYPT_MODE,
          new SecretKeySpec(key, "AES"), new IvParameterSpec(DFT_AES_IV));
      // TODO: workaround to merge bcz cipher.update not work well???
      byte[] plainBytes = getBytes(plainText);
      byte[] saltBytes = genSaltBytes();
      byte[] plainAllBytes = ArrayUtils.addAll(plainBytes, saltBytes);
      byte[] bytes = cipher.doFinal(plainAllBytes);
      return Base64.getEncoder().encodeToString(bytes);
    } catch (Exception ex) {
      throw new UncheckedException(ex);
    }
  }

  public static String aesDecrypt(String encryptedText) {
    return aesDecrypt(encryptedText, DFT_AES_KEY);
  }

  public static String aesDecrypt(String encryptedText, String key) {
    Objects.requireNonNull(key, "arg key");
    return aesDecrypt(encryptedText, getBytes(key));
  }

  private static String aesDecrypt(String encryptedText, byte[] key) {
    Objects.requireNonNull(encryptedText, "arg encryptedText");
    Objects.requireNonNull(key, "arg key");
    try {
      Cipher cipher = Cipher.getInstance(AES_CIPHER);
      cipher.init(Cipher.DECRYPT_MODE,
          new SecretKeySpec(key, "AES"), new IvParameterSpec(DFT_AES_IV));
      byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
      byte[] plainBytes = cipher.doFinal(encryptedBytes);
      return new String(plainBytes, 0, plainBytes.length - SALT_LENGTH, ENCODING);
    } catch (Exception ex) {
      throw new UncheckedException(ex);
    }
  }

  private static byte[] getBytes(String s) {
    try {
      return s.getBytes(ENCODING);
    } catch (Exception ex) {
      throw new UncheckedException(ex);
    }
  }

  private static byte[] genSaltBytes() {
    Random random = new Random();
    byte[] bytes = new byte[SALT_LENGTH];
    random.nextBytes(bytes);
    return bytes;
  }

  /**
   * 使用 MD5 加密，返回十六进制表示的密文字符串。
   */
  public static String md5(String plainText) {
    MessageDigest md5 = null;
    try {
      md5 = MessageDigest.getInstance("MD5");
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
    char[] charArray = plainText.toCharArray();
    byte[] byteArray = new byte[charArray.length];

    for (int i = 0; i < charArray.length; i++) {
      byteArray[i] = (byte) charArray[i];
    }
    byte[] md5Bytes = md5.digest(byteArray);
    StringBuilder hexValue = new StringBuilder();
    for (int i = 0; i < md5Bytes.length; i++) {
      int val = ((int) md5Bytes[i]) & 0xff;
      if (val < 16) {
        hexValue.append("0");
      }
      hexValue.append(Integer.toHexString(val));
    }
    return hexValue.toString();
  }
}
