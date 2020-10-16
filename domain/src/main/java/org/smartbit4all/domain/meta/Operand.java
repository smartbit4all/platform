package org.smartbit4all.domain.meta;

/**
 * This can be a property or an exact value.
 * 
 * @author Peter Boros
 *
 */
public abstract class Operand<T> {

  /**
   * @return Give the current value of the given operand. If it's a value then the value itself if
   *         it's a property then the bound value if it has any. If the property is not bound then
   *         we get back a null.
   */
  public abstract T value();

}
