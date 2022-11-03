package org.smartbit4all.api.object;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.smartbit4all.core.object.ReferenceDefinition;

/**
 * This object contains the information about the change of a reference to an other object.
 * 
 * @author Peter Boros
 *
 */
public class ReferenceValueChange extends ReferenceChangeRequest {

  /**
   * The value for the reference. If it is null then we n
   */
  private ObjectChangeRequest value;

  /**
   * The version uri that is set if we have new object version from
   */
  private URI versionUri;

  public ReferenceValueChange(ApplyChangeRequest request, ObjectChangeRequest objectChangeRequest,
      ReferenceDefinition definition) {
    super(request, objectChangeRequest, definition);
  }

  public final ObjectChangeRequest getValue() {
    return value;
  }

  public final ReferenceValueChange value(ObjectChangeRequest value) {
    this.value = value;
    return this;
  }

  public final ReferenceValueChange operation(ReferenceOperation operation) {
    this.operation = operation;
    return this;
  }

  @Override
  public Iterable<ObjectChangeRequest> changes() {
    return () -> new Iterator<ObjectChangeRequest>() {

      private boolean hasNext = true;

      @Override
      public boolean hasNext() {
        return hasNext;
      }

      @Override
      public ObjectChangeRequest next() {
        if (!hasNext) {
          throw new NoSuchElementException();
        }
        hasNext = false;
        return value;
      }
    };
  }

  @Override
  public void apply(ObjectChangeRequest refererObj, Map<ObjectChangeRequest, URI> uris) {
    URI uri = uris.get(value);
    if (uri != null) {
      refererObj.getObjectAsMap().put(definition.getSourcePropertyPath(), uri);
    }
  }

}
