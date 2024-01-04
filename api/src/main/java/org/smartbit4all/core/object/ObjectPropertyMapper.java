package org.smartbit4all.core.object;

import java.lang.ref.WeakReference;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyMapping;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The object property mapper is the central logic that can help to map values between objects.
 * 
 * @author Peter Boros
 */
public final class ObjectPropertyMapper {

  private static final Logger log = LoggerFactory.getLogger(ObjectPropertyMapper.class);

  private WeakReference<ObjectApi> objectApiRef;

  private ObjectMappingDefinition mapping;

  ObjectPropertyMapper(ObjectApi objectApi, ObjectMappingDefinition mapping) {
    super();
    this.objectApiRef = new WeakReference<>(objectApi);
    this.mapping = mapping;
  }

  private ObjectApi objectApi() {
    return objectApiRef.get();
  }

  public Map<String, Object> copyAllValues(Map<String, Object> from, Map<String, Object> to) {
    ObjectApi objectApi = objectApi();
    for (ObjectPropertyMapping propertyMapping : mapping.getMappings()) {
      Object value = objectApi.getValueFromObjectMap(from,
          StringConstant.toArray(propertyMapping.getSourcePath()));
      objectApi.setValueIntoObjectMap(to, value,
          StringConstant.toArray(propertyMapping.getTargetPath()));
    }
    return to;
  }

}
