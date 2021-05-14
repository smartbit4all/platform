package org.smartbit4all.core.object;

public class ObservableObjectHelper {

  private ObservableObjectHelper() {}

  static boolean pathEquals(ChangeItem item, String path, String name) {
    return upper(item.getPath()).equals(upper(path))
        && upper(item.getName()).equals(upper(name));
  }

  private static String upper(String text) {
    return text == null ? "" : text.toUpperCase();
  }

}
