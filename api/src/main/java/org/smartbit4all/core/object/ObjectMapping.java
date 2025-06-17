package org.smartbit4all.core.object;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.object.bean.ObjectListMapping;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyMapping;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The object property mapper is the central logic that can help to map values between objects.
 * 
 * @author Peter Boros
 */
public final class ObjectPropertyMapper {

  private WeakReference<ObjectApi> objectApiRef;

  private ObjectMappingDefinition mapping;

  ObjectPropertyMapper(ObjectApi objectApi) {
    super();
    this.objectApiRef = new WeakReference<>(objectApi);
  }

  public ObjectPropertyMapper mapping(ObjectMappingDefinition mapping) {
    this.mapping = mapping;
    return this;
  }

  private ObjectApi objectApi() {
    return objectApiRef.get();
  }

  public Map<String, Object> copyAllValues(Map<String, Object> from, Map<String, Object> to) {
    return copyAllValues(mapping, from, to);
  }

  private final Map<String, Object> copyAllValues(ObjectMappingDefinition mappingDef,
      Map<String, Object> from, Map<String, Object> to) {
    ObjectApi objectApi = objectApi();
    for (ObjectPropertyMapping propertyMapping : mappingDef.getMappings()) {
      Object value = objectApi.getValueFromObjectMap(from,
          StringConstant.toArray(propertyMapping.getFromPath()));
      if (value != null) {
        objectApi.setValueIntoObjectMap(to, value,
            StringConstant.toArray(propertyMapping.getToPath()));
      }
    }
    for (ObjectListMapping listMapping : mappingDef.getListMappings()) {
      String[] toListPath = StringConstant.toArray(listMapping.getToListPath());
      Object toValueList = objectApi.getValueFromObjectMap(to,
          toListPath);
      List<Object> toList;
      if (!(toValueList instanceof List)) {
        toList = new ArrayList<>();
      } else {
        toList = (List<Object>) toValueList;
      }
      if (listMapping.getFromPrimitivePath() != null
          && !listMapping.getFromPrimitivePath().isEmpty()) {
        Object value = objectApi.getValueFromObjectMap(from,
            StringConstant.toArray(listMapping.getFromPrimitivePath()));
        if (value != null) {
          toList.add(value);
        }
      } else if (listMapping.getObjectMapping() != null) {
        Map<String, Object> toListItemMap = new HashMap<>();
        toListItemMap = copyAllValues(listMapping.getObjectMapping(), from, toListItemMap);
        if (!toListItemMap.isEmpty()) {
          toList.add(toListItemMap);
        }
      }
      objectApi.setValueIntoObjectMap(to, toList,
          toListPath);
    }
    return to;
  }

}
