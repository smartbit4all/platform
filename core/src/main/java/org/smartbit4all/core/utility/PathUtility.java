package org.smartbit4all.core.utility;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtility {

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
}
