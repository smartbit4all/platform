package org.smartbit4all.domain.meta;

import java.lang.reflect.Method;

/**
 * This method is simply a getter for a {@link Property}.
 * 
 * @author Peter Boros
 */
class EDMProperty extends EntityDefinitionMethod {

  /**
   * The property for the method.
   */
  Property<?> property;

  public EDMProperty(Property<?> property) {
    super();
    this.property = property;
  }

  @Override
  Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return property;
  }

}
