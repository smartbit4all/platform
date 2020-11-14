package org.smartbit4all.api;

/**
 * The change refers the old value and the new value. For an api it's optional to provide this level
 * of information about the modification of an object but this can be important in some cases.
 * 
 * @author Peter Boros
 */
public class ApiItemChange {

  /**
   * The old value of the given change.
   */
  private final Object oldValue;

  /**
   * The new value.
   */
  private final Object newValue;

  public ApiItemChange(Object oldValue, Object newValue) {
    super();
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

}
