package org.smartbit4all.api.object;

import java.util.Map;
import org.smartbit4all.api.object.bean.ObjectChangeData;
import org.smartbit4all.core.object.ObjectNode;

/**
 * The compare api is responsible for comparing the domain objects with different algorithms. There
 * are functions for deep equals and many more.
 *
 * @author Peter Boros
 */
public interface CompareApi {

  ObjectChangeData changes(ObjectNode node1, ObjectNode node2);

  ObjectChangeData changesOfMap(Map<String, Object> map1, Map<String, Object> map2);

  /**
   * Creates a Map from an ObjectChangeData. Keys will be property paths prefixed with path
   * parameter, values will be the old or new values, depending on the newValues parameter.
   *
   * @param objectChange
   * @param path
   * @param newValues result should contain newValues or oldValues
   * @return
   */
  Map<String, Object> toMap(ObjectChangeData objectChange, String path, boolean newValues);
}
