package org.smartbit4all.core.utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.common.base.Strings;

public class PathUtility {

  private PathUtility() {}

  public static String subpath(String path, int beginIndex, int endIndex) {
    Path fullPath = Paths.get(path);
    return fullPath.subpath(beginIndex, endIndex).toString().replace('\\', '/');
  }

  public static String nextFullPath(String path) {
    return subpath(path, 1, getPathSize(path));
  }

  public static String getParentPath(String path) {
    Path fullPath = Paths.get(path);
    return fullPath.getNameCount() == 1 ? null : subpath(path, 0, getPathSize(path) - 1);
  }

  public static int getPathSize(String path) {
    return Paths.get(path).getNameCount();
  }

  public static String getLastPath(String path) {
    Path fullPath = Paths.get(path);
    if (fullPath.getNameCount() < 2) {
      return path;
    }

    return fullPath.getFileName().toString();
  }

  public static String getRootPath(String path) {
    Path fullPath = Paths.get(path);
    if (fullPath.getNameCount() < 2) {
      return path;
    }

    return fullPath.iterator().next().toString();
  }

  public static String concatPath(String path1, String path2) {
    String result = "";
    if (!Strings.isNullOrEmpty(path1)) {
      result += path1;
      if (!path1.endsWith(StringConstant.SLASH)) {
        result += StringConstant.SLASH;
      }
    }
    if (!Strings.isNullOrEmpty(path2)) {
      result += path2;
    }

    return result;
  }

  public static String concatPath(boolean includeLast, String... path) {
    StringBuilder sb = new StringBuilder();
    int max = includeLast ? path.length : path.length - 1;
    for (int i = 0; i < max; i++) {
      String part = path[i];
      if (!Strings.isNullOrEmpty(part)) {
        if (sb.length() != 0) {
          sb.append("/");
        }
        sb.append(part);
      }
    }
    return sb.toString();
  }

  public static String[] decomposePath(String path) {
    if (Strings.isNullOrEmpty(path)) {
      return new String[0];
    }
    String[] split = path.split("/");
    return split;
  }
}
