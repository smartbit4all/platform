package org.smartbit4all.core.object;

import java.util.Objects;
import com.google.common.base.Strings;

public class ObservableObjectHelper {

  private ObservableObjectHelper() {}

  static boolean pathMatch(ChangeItem changeItem, String path, String name) {
    if (Strings.isNullOrEmpty(path) && Strings.isNullOrEmpty(changeItem.getPath())) {
      // empty paths are considered equal, check only name
      return Objects.equals(changeItem.getName(), name);
    }
    return Objects.equals(changeItem.getPath(), path)
        && Objects.equals(changeItem.getName(), name);
  }
}
