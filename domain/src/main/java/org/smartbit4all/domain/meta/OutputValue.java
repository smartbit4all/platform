package org.smartbit4all.domain.meta;

/**
 * The output value is hiding the knowledge of how to set the property for a given computation.
 * 
 * @author Peter Boros
 * @param <T> The type of the value.
 */
public interface OutputValue<T> {

  /**
   * The computation can set the value of the given property.
   * 
   * @param value
   */
  public abstract void set(T value);

  /**
   * Defines the property the value comes from.
   * 
   * @return
   */
  Property<T> property();

}
