package lucky.sky.util.io;

import java.io.File;
import java.nio.file.Paths;


public final class Path {

  /**
   * 连接文件路径
   */
  public static String join(String first, String... more) {
    return Paths.get(first, more).toString();
  }


  /**
   * 获取扩展名
   */
  public static String getExtension(String path) {
//        System.out.print(path + ":" + path.length());
    for (int i = path.length() - 1; i >= 0; i--) {
      char ch = path.charAt(i);
//            System.out.println("i:" + ch);
      if (ch == '.') {
        return path.substring(i);
      } else if (ch == File.pathSeparatorChar) {
        break;
      }
    }
    return "";
  }
}
