package org.smartbit4all.domain.meta;

import java.lang.reflect.Method;

/**
 * The method returns the {@link Reference} of a {@link EntityDefinition}.
 * 
 * @author Peter Boros
 */
class EDMReference extends EntityDefinitionMethod {

  /**
   * The referenced entity.
   */
  EntityDefinition referencedEntity;

  /**
   * The reference for the given entity.
   */
  Reference<?, ?> reference;

  public EDMReference(EntityDefinition referencedEntity, Reference<?, ?> reference) {
    super();
    this.referencedEntity = referencedEntity;
    this.reference = reference;
  }

  @Override
  Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return reference;
  }

}
