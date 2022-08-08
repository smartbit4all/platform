package org.smartbit4all.api.navigation;

import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.core.object.ReferenceDefinition;

/**
 * The descriptor of a navigation reference. Contains the association meta and the reference
 * definition to support the navigation algorithm.
 * 
 * @author Peter Boros
 */
final class ObjectNavigationReference {

  public ObjectNavigationReference(NavigationAssociationMeta associationMeta,
      ReferenceDefinition referenceDefinition) {
    super();
    this.associationMeta = associationMeta;
    this.referenceDefinition = referenceDefinition;
  }

  NavigationAssociationMeta associationMeta;

  ReferenceDefinition referenceDefinition;

}
