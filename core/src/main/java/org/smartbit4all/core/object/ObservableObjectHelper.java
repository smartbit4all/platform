package org.smartbit4all.core.object;

import java.util.Objects;
import com.google.common.base.Strings;

public class ObservableObjectHelper {

  private ObservableObjectHelper() {}

  static boolean pathMatch(ChangeItem changeItem, String path, String name) {
    String changeItemNameUpper = upper(changeItem.getName());
    String changeItemPathUpper = upper(changeItem.getPath());
    
    String nameUpper = upper(name);
    String pathUpper = upper(path);
    
    if (Strings.isNullOrEmpty(pathUpper) && Strings.isNullOrEmpty(changeItemPathUpper)) {
      // empty paths are considered equal, check only name
      return Objects.equals(changeItemNameUpper, nameUpper);
    }
    
    return Objects.equals(changeItemPathUpper, pathUpper)
        && Objects.equals(changeItemNameUpper, nameUpper);
  }
  
  private static String upper(String text) {
    return text == null ? null : text.toUpperCase();
  }
  
}
