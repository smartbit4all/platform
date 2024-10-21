package org.smartbit4all.core.object;

import org.smartbit4all.core.utility.StringConstant;

public class PathProcessor {

  private String[] parts;

  private PathProcessor() {}

  private PathProcessor(String path) {
    this.parts = path.split(StringConstant.DOT_REGEX);
  }

  public static PathProcessor of(String path) {
    return new PathProcessor(path);
  }

  /**
   * Returns the first element of the path
   * 
   * @return
   */
  public String firstElement() {
    if (parts.length > 0) {
      return parts[0];
    }
    return "";
  }

  /**
   * Returns the last element of the path
   * 
   * @return
   */
  public String lastElement() {
    if (parts.length > 0) {
      return parts[parts.length - 1];
    }
    return parts[0];
  }

  /**
   * Get the ending of the path (all elements except the first one)
   * 
   * @return
   */
  public String ending() {
    if (parts.length > 1) {
      StringBuilder sb = new StringBuilder();
      for (int i = 1; i < parts.length; i++) {
        if (i > 1) {
          sb.append(".");
        }
        sb.append(parts[i]);
      }
      return sb.toString();
    }
    return "";
  }

  /**
   * Get the beginning of the path (all elements except the last one)
   * 
   * @return
   */
  public String beginning() {
    if (parts.length > 1) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < parts.length - 1; i++) {
        if (i > 0) {
          sb.append(".");
        }
        sb.append(parts[i]);
      }
      return sb.toString();
    }
    return "";
  }

  /**
   * Returns true if the path has more than 1 element
   * 
   * @return
   */
  public boolean multiLevel() {
    return parts.length > 1;
  }


}
