package org.smartbit4all.api.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.smartbit4all.api.object.bean.ObjectChangeData;
import org.smartbit4all.api.object.bean.PropertyChangeData;
import org.smartbit4all.api.object.bean.ReferenceChangeData;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public class CompareApiImpl implements CompareApi {

  @Autowired
  ObjectApi objectApi;

  @Override
  public ObjectChangeData changes(ObjectNode node1, ObjectNode node2) {
    Map<String, Object> map1 = node1 == null ? new HashMap<>() : node1.getObjectAsMap();
    Map<String, Object> map2 = node2 == null ? new HashMap<>() : node2.getObjectAsMap();
    return changesOfMap(map1, map2);
  }

  private final ObjectChangeData changesOfMap(Map<String, Object> map1,
      Map<String, Object> map2) {
    List<String> deletedEntries = new ArrayList<>();
    List<String> changeCandidateEntries = new ArrayList<>();
    for (Entry<String, Object> entry : map1.entrySet()) {
      if (map2.containsKey(entry.getKey())) {
        changeCandidateEntries.add(entry.getKey());
      } else {
        deletedEntries.add(entry.getKey());
      }
    }
    Map<String, Object> map2Tmp = new HashMap<>(map2);
    ObjectChangeData result = new ObjectChangeData();

    for (String key : changeCandidateEntries) {
      Object value1 = map1.get(key);
      Object value2 = map2Tmp.remove(key);
      String subPath = pathOf(key);
      if (value1 instanceof Map || value2 instanceof Map) {
        result.addReferencesItem(referenceChangeOf(value1, value2, subPath));
      } else {
        if (!Objects.deepEquals(value1, value2)) {
          result.addPropertiesItem(
              new PropertyChangeData().path(subPath).oldValue(value1).newValue(value2));
        }
      }
    }
    for (String key : deletedEntries) {
      Object value1 = map1.get(key);
      if (value1 instanceof Map) {
        result.addReferencesItem(referenceChangeOf(value1, null, pathOf(key)));
      } else {
        if (value1 != null) {
          result.addPropertiesItem(
              new PropertyChangeData().path(pathOf(key)).oldValue(value1)
                  .newValue(null));

        }
      }
    }
    for (Entry<String, Object> entry : map2Tmp.entrySet()) {
      Object value2 = entry.getValue();
      if (value2 instanceof Map) {
        result.addReferencesItem(referenceChangeOf(null, value2, pathOf(entry.getKey())));
      } else {
        if (value2 != null) {
          result.addPropertiesItem(
              new PropertyChangeData().path(pathOf(entry.getKey())).oldValue(null)
                  .newValue(value2));
        }
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private final ReferenceChangeData referenceChangeOf(Object value1, Object value2,
      String subPath) {
    return new ReferenceChangeData().path(subPath)
        .objectChange(changesOfMap(
            value1 instanceof Map ? (Map<String, Object>) value1 : Collections.emptyMap(),
            value2 instanceof Map ? (Map<String, Object>) value2 : Collections.emptyMap()));
  }

  private static final String pathOf(String path) {
    return path;
  }

}
