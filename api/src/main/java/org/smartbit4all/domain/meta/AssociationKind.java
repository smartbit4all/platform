package org.smartbit4all.domain.meta;

/**
 * The different kind of associations defined in this enumeration. The retrieval mechanism is built
 * upon this information. We can decides how to navigate through an association based on this kind
 * and other parameters.
 * 
 * @author Peter Boros
 *
 */
public enum AssociationKind {

  /**
   * This implementation is based on a {@link Reference} point to the key of the other entity. This
   * {@link AssociationDefinition} can be binary only.
   */
  REFERENCE,
  /**
   * The association means that we have an individual association entity with References to every
   * related instance. So it can be an N-ary association.
   */
  ASSOCIATIONENTITY

}
