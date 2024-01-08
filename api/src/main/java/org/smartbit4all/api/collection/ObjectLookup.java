package org.smartbit4all.api.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.api.collection.bean.ObjectLookupParameter;
import org.smartbit4all.api.collection.bean.ObjectLookupResult;
import org.smartbit4all.api.collection.bean.ObjectLookupResultItem;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectPropertyMapper;
import org.smartbit4all.core.utility.StringConstant;
import static java.util.stream.Collectors.joining;

/**
 * The {@link ObjectLookup} is a generic abstract class for lookup a collection by the values of an
 * object. The lookup could be necessary if the original object has some values but not all and we
 * need to fill the extended the properties via the values of the object found by the lookup
 * mechanism. The lookup is always based on a collection that we have. Like a {@link StoredMap}
 * where the lookup works on the key of the map. In this case you can parameterize only the name of
 * the property that should be used from the object to lookup in the map. So we can initiate an
 * {@link ObjectLookup} via one of the collections managed by the {@link CollectionApi} or directly
 * from the api itself.
 * 
 * @author Peter Boros
 */
public abstract class ObjectLookup {

  private final ObjectApi objectApi;

  protected ObjectLookup(ObjectApi objectApi) {
    super();
    this.objectApi = objectApi;
  }

  /**
   * The lookup tries to identify the related objects from the collection it was created by.
   * 
   * @param valueObject The input object to lookup for. We use the properties of this Object. The
   *        Object can be a {@link Map}, {@link ObjectNode}. In both case we can have the proper
   *        values by the {@link ObjectApi} that is injected by the {@link CollectionApi} to the
   *        instance.
   * @param parameter The look parameters.
   * @return The lookup result.
   */
  public abstract ObjectLookupResult lookup(Object valueObject,
      ObjectLookupParameter parameter);

  /**
   * Walks through on the list of objects and call the lookup for all of them.
   * 
   * @param objects The objects that can be a {@link Map} or {@link ObjectNode}. In both case we can
   *        have the proper values by the {@link ObjectApi} that is injected by the
   *        {@link CollectionApi} to the instance.
   * @param parameter The lookup parameters.
   * @param mapping The mapping parameters for copying the result back to the original object.
   */
  @SuppressWarnings("unchecked")
  public final void fillObjects(List<Object> objects,
      ObjectLookupParameter parameter, ObjectMappingDefinition mapping) {
    Objects.requireNonNull(objects);
    Objects.requireNonNull(parameter);
    Objects.requireNonNull(mapping);
    ObjectPropertyMapper mapper = objectApi.mapper().mapping(mapping);
    for (Object object : objects) {
      ObjectLookupResult lookupResult = lookup(object, parameter);
      if (!lookupResult.getItems().isEmpty()) {
        // Now we set the most relevant result item without any further examination.
        ObjectLookupResultItem lookupResultItem = lookupResult.getItems().get(0);
        Map<String, Object> toMap;
        if (object instanceof ObjectNode) {
          toMap = ((ObjectNode) object).getObjectAsMap();
        } else {
          toMap = (Map<String, Object>) object;
        }
        mapper.copyAllValues(lookupResultItem.getObjectAsMap(), toMap);
      }
    }
  }

  public Map<String, Object> findByUnique(ObjectPropertyValue value) {
    if (value == null) {
      return null;
    }
    Map<String, Object> valueObject = new HashMap<>();
    valueObject.put(value.getPath().stream().collect(joining(StringConstant.SPACE_HYPHEN_SPACE)),
        value.getValue());
    ObjectLookupResult lookupResult = lookup(valueObject, new ObjectLookupParameter().limit(1));
    return lookupResult.getItems().isEmpty() ? null
        : lookupResult.getItems().get(0).getObjectAsMap();
  }

}
