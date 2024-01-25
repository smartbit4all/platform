package org.smartbit4all.api.object;

import java.util.Map;
import org.smartbit4all.api.object.bean.ObjectChangeData;
import org.smartbit4all.api.object.bean.PropertyChangeData;
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
   * @param objectChange the {@link ObjectChangeData} derived from comparing two objects, by either
   *        invoking {@link #changes(ObjectNode, ObjectNode)} or {@link #changesOfMap(Map, Map)}
   * @param path a {@code String} prefix to use in every key
   * @param newValues result should contain newValues or oldValues
   * @return a {@link Map} containing the flattened changes extracted from change data, with
   *         absolute path as keys, and either new or old values of the respective properties as
   *         values
   * @see #flattenChanges(ObjectChangeData, String)
   */
  Map<String, Object> toMap(ObjectChangeData objectChange, String path, boolean newValues);

  /**
   * Creates a flat {@link Map} from an {@link ObjectChangeData}.
   * 
   * <p>
   * Values of this map contain both old and new values wrapped in {@PropertyChangeData}. The map is
   * considered flat as all enumerated changes are not complex, but primitive-like (such as numbers,
   * strings, URIs, UUIDs etc.).
   * 
   * @param objectChange the {@link ObjectChangeData} derived from comparing two objects, by either
   *        invoking {@link #changes(ObjectNode, ObjectNode)} or {@link #changesOfMap(Map, Map)}
   * @param pathPrefix a {@code String} prefix to use in every key
   * @return a {@link Map} containing the flattened changes extracted from change data, with
   *         absolute path as keys, and {@link PropertyChangeData} as values, containing both old
   *         and new values for a given property
   */
  Map<String, PropertyChangeData> flattenChanges(ObjectChangeData objectChange, String pathPrefix);
}
