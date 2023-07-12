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
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class CompareApiImpl implements CompareApi {

  @Autowired
  ObjectApi objectApi;

  @Override
  public ObjectChangeData changes(ObjectNode node1, ObjectNode node2) {
    Map<String, Object> map1 = node1 == null ? new HashMap<>() : node1.getObjectAsMap();
    checkReferences(map1, node1);
    Map<String, Object> map2 = node2 == null ? new HashMap<>() : node2.getObjectAsMap();
    checkReferences(map2, node2);
    return changesOfMap(map1, map2);
  }

  // if the ObjectNode has references then use those instead of the reference URIs
  private void checkReferences(Map<String, Object> map, ObjectNode node) {
    if (node != null) {
      for (Entry<String, ObjectNodeReference> referenceEntries : node.getReferences().entrySet()) {
        if (referenceEntries.getValue().get() != null) {
          map.put(referenceEntries.getKey(), referenceEntries.getValue().get().getObjectAsMap());
        }
      }
    }
  }

  @Override
  public final ObjectChangeData changesOfMap(Map<String, Object> map1,
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
      } else if (value1 instanceof List || value2 instanceof List) {
        result.addReferencesItem(listChangeOf(value1, value2, subPath));
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
      } else if (value1 instanceof List) {
        result.addReferencesItem(listChangeOf(value1, null, pathOf(key)));
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
      } else if (value2 instanceof List) {
        result.addReferencesItem(listChangeOf(null, value2, pathOf(entry.getKey())));
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

  /**
   * Creates ObjectChangeData for two lists. The path will be the object's index in the list.
   *
   * @return
   */
  private ObjectChangeData changesOfList(List<Object> list1,
      List<Object> list2) {
    ObjectChangeData result = new ObjectChangeData();
    for (int i = 0; i < list1.size(); ++i) {
      Object value1 = list1.get(i);
      Object value2 = list2.size() > i ? list2.get(i) : null;
      String subPath = String.valueOf(i);
      if (value1 instanceof Map || value2 instanceof Map) {
        result.addReferencesItem(referenceChangeOf(value1, value2, subPath));
      } else if (value1 instanceof List || value2 instanceof List) {
        result.addReferencesItem(listChangeOf(value1, value2, subPath));
      } else {
        if (!Objects.deepEquals(value1, value2)) {
          result.addPropertiesItem(
              new PropertyChangeData().path(subPath).oldValue(value1).newValue(value2));
        }
      }
    }

    if (list2.size() > list1.size()) {
      for (int i = list1.size(); i < list2.size(); ++i) {
        Object value2 = list2.get(i);

        String subPath = String.valueOf(i);
        if (value2 instanceof Map) {
          result.addReferencesItem(referenceChangeOf(null, value2, subPath));
        } else if (value2 instanceof List) {
          result.addReferencesItem(listChangeOf(null, value2, subPath));
        } else {
          result.addPropertiesItem(
              new PropertyChangeData().path(subPath).oldValue(null).newValue(value2));
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

  @SuppressWarnings("unchecked")
  private final ReferenceChangeData listChangeOf(Object value1, Object value2,
      String subPath) {
    return new ReferenceChangeData().path(subPath)
        .objectChange(changesOfList(
            value1 instanceof List ? (List<Object>) value1 : Collections.emptyList(),
            value2 instanceof List ? (List<Object>) value2 : Collections.emptyList()));
  }

  private static final String pathOf(String path) {
    return path;
  }

  @Override
  public Map<String, Object> toMap(ObjectChangeData objectChange, String path, boolean newValues) {
    Map<String, Object> changes = new HashMap<>();
    addChanges(objectChange, path, changes, newValues);
    return changes;
  }

  private void addChanges(ObjectChangeData objectChange, String path, Map<String, Object> changes,
      boolean newValues) {
    String pathPrefix = Strings.isNullOrEmpty(path) ? "" : path + StringConstant.DOT;
    for (PropertyChangeData prop : objectChange.getProperties()) {
      String key = pathPrefix + prop.getPath();
      if (newValues) {
        changes.put(key, prop.getNewValue());
      } else {
        changes.put(key, prop.getOldValue());
      }
    }
    for (ReferenceChangeData ref : objectChange.getReferences()) {
      String key = pathPrefix + ref.getPath();
      addChanges(ref.getObjectChange(), key, changes, newValues);
    }
  }

}
