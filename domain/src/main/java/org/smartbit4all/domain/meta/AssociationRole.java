/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.meta;

/**
 * 
 * This is the association end meta class that describe the properties of the association end.
 * 
 * @author Peter Boros
 *
 */
public final class AssociationRole {

  /**
   * The owner association.
   */
  private AssociationDefinition association;

  /**
   * The {@link EntityDefinition} belong to the role of the association.
   */
  private EntityDefinition entity;

  /**
   * The multiplicity of the role.
   */
  private Multiplicity multiplicity;

  /**
   * True if the role is easily accessible through the association.
   */
  private boolean navigable;

  /**
   * If the {@link AssociationDefinition} is has an {@link AssociationKind#REFERENCE} kind then
   * implementer {@link Reference} can be accessed from the role. This {@link Reference} belongs to
   * the {@link #entity}.
   */
  private Reference<?, ?> reference;

  AssociationRole(AssociationDefinition association, EntityDefinition entity,
      Reference<?, ?> reference,
      Multiplicity multiplicity, boolean navigable) {
    super();
    this.association = association;
    this.entity = entity;
    this.reference = reference;
    this.multiplicity = multiplicity;
    this.navigable = navigable;
  }

  public final AssociationDefinition getAssociation() {
    return association;
  }

  public final void setAssociation(AssociationDefinition association) {
    this.association = association;
  }

  public final EntityDefinition getEntity() {
    return entity;
  }

  final void setEntity(EntityDefinition entity) {
    this.entity = entity;
  }

  public final Multiplicity getMultiplicity() {
    return multiplicity;
  }

  final void setMultiplicity(Multiplicity multiplicity) {
    this.multiplicity = multiplicity;
  }

  public final boolean isNavigable() {
    return navigable;
  }

  final void setNavigable(boolean navigable) {
    this.navigable = navigable;
  }

  public final Reference<?, ?> getReference() {
    return reference;
  }

}
