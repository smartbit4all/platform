package org.smartbit4all.api.applychange;

import java.util.Map;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ReferenceDefinition;

/**
 * The change request for one object with a required operation. The object itself can be directly a
 * {@link Map} or the java object. If we set the java object then we don't use the
 * {@link #objectAsMap}!
 * 
 * @author Peter Boros
 */
public class ObjectChangeRequest {

  /**
   * The object definition to access the meta of the given object.
   */
  private final ObjectDefinition<?> definition;

  public ObjectChangeRequest(ObjectDefinition<?> definition) {
    super();
    this.definition = definition;
  }

  /**
   * The object values of the request. The existing properties of the map are going to set during
   * the save and the rest of the property value will remain the same. If the {@link #object} is set
   * then we don't use this map!
   */
  private Map<String, Object> objectAsMap;

  /**
   * If we have the object the {@link #objectAsMap} is not used!
   */
  private Object object;

  /**
   * The reference changes for the object.
   */
  private Map<ReferenceDefinition, ReferenceChangeRequest> referenceChanges;

  public ReferenceValueChange referenceValue(ReferenceDefinition definition) {
    return (ReferenceValueChange) referenceChanges.computeIfAbsent(definition,
        d -> new ReferenceValueChange(definition));
  }

}
