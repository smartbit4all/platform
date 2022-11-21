package org.smartbit4all.core.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.smartbit4all.core.utility.PathUtility;
import com.google.common.base.Strings;

public class ObservableObjectHelper {

  private ObservableObjectHelper() {}

  static boolean pathEquals(ChangeItem item, ObjectPropertyPath path) {
    return upper(item.getPath()).equals(upper(path.path))
        && upper(item.getName()).equals(upper(path.property));
  }

  private static String upper(String text) {
    return text == null ? "" : text.toUpperCase();
  }

  static String[] concat(String path, String[] paths) {
    int size = paths == null ? 1 : paths.length + 1;
    String[] result = new String[size];
    result[0] = path;
    if (paths != null) {
      for (int i = 0; i < paths.length; i++) {
        result[i + 1] = paths[i];
      }
    }
    return result;
  }

  static String removeParentPath(String path, String parentPath) {
    if (path == null || parentPath == null) {
      return path;
    }
    if (path.startsWith(parentPath)) {
      return path.substring(parentPath.length());
    }
    return path;
  }

  static class ObjectPropertyPath {
    /**
     * Path within the object, separated by "/" characters.
     */
    String path;

    /**
     * Name of the property / reference / collection.
     */
    String property;

    @Override
    public String toString() {
      return path + "." + property;
    }
  }

  static ObjectPropertyPath processPathParameter(String... propertyPath) {
    // should be non-empty
    if (propertyPath == null || propertyPath.length == 0) {
      throw new IllegalArgumentException(
          "No path was given when subscribing to observable object changes!");
    }
    // first item can be null or empty string - skip it
    if (Strings.isNullOrEmpty(propertyPath[0])) {
      if (propertyPath.length == 1) {
        throw new IllegalArgumentException(
            "Empty path was given when subscribing to observable object changes!");
      }
    }
    // process all path parts: first split by "/"
    List<String[]> processedPathsList = new ArrayList<>();
    for (int i = 0; i < propertyPath.length; i++) {
      processedPathsList.add(PathUtility.decomposePath(propertyPath[i]));
    }
    // then collect all parts, ignore empty ones
    String[] processedPaths = processedPathsList.stream()
        .flatMap(Arrays::stream)
        .filter(part -> !Strings.isNullOrEmpty(part))
        .toArray(String[]::new);

    ObjectPropertyPath path = new ObjectPropertyPath();
    path.path = PathUtility.concatPath(false, processedPaths);
    path.property = processedPaths[processedPaths.length - 1];
    return path;
  }
}
