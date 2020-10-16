package org.smartbit4all.domain.meta;

/**
 * The input value is hiding the knowledge of how to access the input property for a given
 * computation.
 * 
 * TODO The toString must be implemented to be able to use the String value without get()!
 * 
 * @author Peter Boros
 * @param <T> The type of the value.
 */
public interface InputValue<T> {

  /**
   * Retrieves the value of the dependent property when the computation is running
   * 
   * @return
   */
  public abstract T get();

  @Override
  String toString();

  /**
   * Defines the property the value comes from.
   * 
   * @return
   */
  Property<T> property();

}
