package org.smartbit4all.domain.meta;

/**
 * The association is the relation between {@link EntityDefinition}-s as described by the OO theory.
 * The association has two reference for one to other and back. And the association can have
 * additional entity representing the information related to the association. It's optional. The
 * association is also generated from the model because it's part of the domain definition we have.
 * The association is typed for being a better programming API.
 * 
 * @author Peter Boros
 */
public class Association<E1 extends EntityDefinition, E2 extends EntityDefinition> {

  /**
   * The association is generated in one selected direction. It's typically the navigation.
   */
  Reference<E1, E2> E1toE2;

  /**
   * The association is generated in one selected direction. It's typically the navigation.
   */
  Reference<E2, E1> E2ToE1;

  /**
   * The references in a typeless way.
   */
  Reference<?, ?> references[] = new Reference<?, ?>[2];

}
