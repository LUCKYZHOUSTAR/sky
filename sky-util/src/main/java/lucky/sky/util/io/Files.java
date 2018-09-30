package lucky.sky.util.io;

import lucky.sky.util.lang.StrKit;

import java.io.File;

/**
 * Utilities for file operation.
 */
public class Files {

  /**
   * 检测指定的路径是否存在。
   */
  public static boolean exists(String path) {
    if (StrKit.isBlank(path)) {
      return false;
    }
    File file = new File(path);
    return file.exists();
  }

  public static void mkDir(File file) {
    if (file.getParentFile().exists()) {
      file.mkdir();
    } else {
      mkDir(file.getParentFile());
      file.mkdir();
    }
  }


  public static void main(String[] args) {
    String path="/home/hnair/logs/hnair.order.service/2018-04-12/debug.info";

    File file=new File(path);
    System.out.println(file.getParent());
    System.out.println(file.exists());
    mkDir(file);

  }
}
