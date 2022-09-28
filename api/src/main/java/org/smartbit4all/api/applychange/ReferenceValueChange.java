package org.smartbit4all.api.applychange;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.smartbit4all.core.object.ReferenceDefinition;

public class ReferenceValueChange extends ReferenceChangeRequest {

  /**
   * The value for the reference. If it is null then we n
   */
  private ObjectChangeRequest value;

  /**
   * The version uri that is set if we have new object version from
   */
  private URI versionUri;

  /**
   * The operation for the reference value.
   */
  private ReferenceValueOperation operation;

  public ReferenceValueChange(ReferenceDefinition definition) {
    super(definition);
  }

  public enum ReferenceValueOperation {
    SET, REMOVE, UPDATE
  }

  public final ObjectChangeRequest getValue() {
    return value;
  }

  public final ReferenceValueChange value(ObjectChangeRequest value) {
    this.value = value;
    return this;
  }

  public final ReferenceValueOperation getOperation() {
    return operation;
  }

  public final ReferenceValueChange operation(ReferenceValueOperation operation) {
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
