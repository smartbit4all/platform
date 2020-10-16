package org.smartbit4all.domain.meta;

import java.lang.reflect.Method;

/**
 * A getter for the referenced entity with a given reference.
 * 
 * @author Peter Boros
 */
class EDMReferencedEntity extends EntityDefinitionMethod {

  /**
   * The referenced entity.
   */
  EntityDefinition referencedEntity;

  /**
   * The reference for the given entity.
   */
  Reference<?, ?> reference;

  public EDMReferencedEntity(EntityDefinition referencedEntity, Reference<?, ?> reference) {
    super();
    this.referencedEntity = referencedEntity;
    this.reference = reference;
  }

  @Override
  Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return referencedEntity;
  }

}
