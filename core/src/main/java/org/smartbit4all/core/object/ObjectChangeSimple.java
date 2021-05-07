package org.smartbit4all.core.object;

import org.smartbit4all.core.utility.StringConstant;

/**
 * Represents a changed object in a simple way, without any property/reference/collection change
 * detail. Only the changed object is present (newValue). Returned object should be treated as
 * immutable (future implementation will throw UnsupportedException when trying to change this
 * object)!
 *
 * Typically used in CollectionObjectChange handling scenario, when subscribing to a collection
 * change without requiring all the detailed information about the change.
 * 
 * @author Attila Mate
 */
public class ObjectChangeSimple {

  private final String path;

  private final ChangeState operation;

  private final Object object;

  public ObjectChangeSimple(String path, ChangeState operation, Object object) {
    this.path = path;
    this.operation = operation;
    this.object = object;
  }

  public String getPath() {
    return path;
  }

  public final ChangeState getOperation() {
    return operation;
  }

  public Object getObject() {
    return object;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(operation);
    sb.append(StringConstant.COLON_SPACE);
    sb.append(object == null ? StringConstant.NULL : object.toString());
    return sb.toString();
  }
}
