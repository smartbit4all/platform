package org.smartbit4all.api.object;

import java.net.URI;

/**
 * The property change event for the property of an instance. The property change can be primitive
 * type but can be a reference for another object.
 * 
 * @author Peter Boros
 */
public class PropertyChange<P> extends ChangeItem {

  /**
   * The old value of the given property. The old value can be null if we have no idea what was the
   * original value.
   */
  private final P oldValue;

  /**
   * The new value of the property is mandatory.
   */
  private final P newValue;

  PropertyChange(URI parentObjectURI, String name, P oldValue, P newValue) {
    super(parentObjectURI, name);
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  /**
   * The old value of the given property. It's optional because we might have no idea about the old
   * value.
   */
  public final P getOldValue() {
    return oldValue;
  }

  /**
   * The new value of the property that is mandatory.
   */
  public final P getNewValue() {
    return newValue;
  }

}
