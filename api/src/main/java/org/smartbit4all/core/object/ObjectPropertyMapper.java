package org.smartbit4all.core.object;

import java.lang.ref.WeakReference;
import java.util.Map;
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
    ObjectApi objectApi = objectApi();
    for (ObjectPropertyMapping propertyMapping : mapping.getMappings()) {
      Object value = objectApi.getValueFromObjectMap(from,
          StringConstant.toArray(propertyMapping.getFromPath()));
      objectApi.setValueIntoObjectMap(to, value,
          StringConstant.toArray(propertyMapping.getToPath()));
    }
    return to;
  }

}
