package org.smartbit4all.core.object;

import org.smartbit4all.core.object.ObservableObjectImpl.ObjectPropertyPath;

public class ObservableObjectHelper {

  private ObservableObjectHelper() {}

  static boolean pathEquals(ChangeItem item, ObjectPropertyPath path) {
    return upper(item.getPath()).equals(upper(path.path))
        && upper(item.getName()).equals(upper(path.property));
  }

  private static String upper(String text) {
    return text == null ? "" : text.toUpperCase();
  }

}
