package lucky.sky.util.lang;

import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 */
public final class Classes {

  private static Logger logger = LoggerManager.getLogger(Classes.class);

  /**
   * 获取某包下所有类
   *
   * @param pkgName 包名
   * @param recursive 是否遍历子包
   * @return 类的完整名称 <p> 参考实现: http://blog.csdn.net/wangpeng047/article/details/8206427
   */
  public static List<String> getClassListByPackage(String pkgName, boolean recursive) {
    List<String> list = null;
    String regularPkgName = pkgName.replace(".", "/");
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL url = loader.getResource(regularPkgName);
    if (url != null) {
      String type = url.getProtocol();
      if ("file".equals(type)) {
        String path = new File(url.getPath()).getPath();
        list = getClassListByFile(path.substring(0, path.length() - regularPkgName.length()),
            url.getPath(), recursive);
      } else if ("jar".equals(type)) {
        list = getClassListByJar(url.getPath(), recursive);
      }
    } else {
      list = getClassListByJars(((URLClassLoader) loader).getURLs(), regularPkgName, recursive);
    }
    return list;
  }

  /**
   * 从项目文件获取某包下所有类
   *
   * @param filePath 文件路径
   * @param recursive 是否遍历子包
   * @return 类的完整名称
   */
  private static List<String> getClassListByFile(String rootPath, String filePath,
      boolean recursive) {
    List<String> list = new ArrayList<>();

    File file = new File(filePath);
    File[] childFiles = file.listFiles();
    for (File childFile : childFiles) {
      if (childFile.isDirectory()) {
        if (recursive) {
          list.addAll(getClassListByFile(rootPath, childFile.getPath(), recursive));
        }
      } else {
        String childFilePath = childFile.getPath();
        if (childFilePath.endsWith(".class")) {
          childFilePath = childFilePath.substring(rootPath.length());
          childFilePath = childFilePath
              .substring(0, childFilePath.length() - 6); // remove .class postfix
          childFilePath = childFilePath.replace(File.separator, ".");
          list.add(childFilePath);
        }
      }
    }

    return list;
  }

  /**
   * 从jar获取某包下所有类
   *
   * @param jarPath jar文件路径
   * @param recursive 是否遍历子包
   * @return 类的完整名称
   */
  private static List<String> getClassListByJar(String jarPath, boolean recursive) {
    List<String> list = new ArrayList<>();

    String[] jarInfo = jarPath.split("!");
    String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
    String packagePath = jarInfo[1].substring(1);
    try (JarFile jarFile = new JarFile(URLDecoder.decode(jarFilePath, "utf-8"))) {
      Enumeration<JarEntry> entrys = jarFile.entries();
      while (entrys.hasMoreElements()) {
        JarEntry jarEntry = entrys.nextElement();
        String entryName = jarEntry.getName();
        if (entryName.endsWith(".class")) {
          addClass(recursive, list, packagePath, entryName);
        }
      }
    } catch (Exception e) {
      logger.warn("", e);
    }

    return list;
  }

  private static void addClass(boolean recursive, List<String> list, String packagePath,
      String entryName) {
    if (recursive) {
      if (entryName.startsWith(packagePath)) {
        entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
        list.add(entryName);
      }
    } else {
      int index = entryName.lastIndexOf("/");
      String myPackagePath;
      if (index != -1) {
        myPackagePath = entryName.substring(0, index);
      } else {
        myPackagePath = entryName;
      }
      if (myPackagePath.equals(packagePath)) {
        entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
        list.add(entryName);
      }
    }
  }

  /**
   * 从所有jar中搜索该包，并获取该包下所有类
   *
   * @param urls URL集合
   * @param pkgName 包路径
   * @param recursive 是否遍历子包
   * @return 类的完整名称
   */
  private static List<String> getClassListByJars(URL[] urls, String pkgName, boolean recursive) {
    List<String> list = new ArrayList<>();

    if (urls != null) {
      for (int i = 0; i < urls.length; i++) {
        URL url = urls[i];
        String urlPath = url.getPath();
        // 不必搜索文件夹
        if (urlPath.endsWith("/")) {
          continue;
        }

        String jarPath = urlPath + "!/" + pkgName;
        list.addAll(getClassListByJar(jarPath, recursive));
      }
    }

    return list;
  }
}
