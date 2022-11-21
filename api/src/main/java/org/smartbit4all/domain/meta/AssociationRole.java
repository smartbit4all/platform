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
   * The unique name of the role inside the association.
   */
  private String name;

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
   * If the {@link AssociationDefinition} is has an {@link AssociationKind#REFERENCE} kind then
   * implementer {@link Reference} can be accessed from the role. We can have reference directly
   * from one entity to another. In this case the association is based on the reference itself.
   * Otherwise if we have an association entity then this is the reference from the association
   * entity to the entity of the role.
   */
  private Reference<?, ?> reference;

  /**
   * True if the entity identified by the role is referred by the {@link #reference}. If this is the
   * referrer the it's false.
   */
  private boolean referred = true;

  AssociationRole(String name, AssociationDefinition association, EntityDefinition entity,
      Reference<?, ?> reference,
      Multiplicity multiplicity, boolean referred) {
    super();
    this.name = name;
    this.association = association;
    this.entity = entity;
    this.reference = reference;
    this.multiplicity = multiplicity;
    this.referred = referred;
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

  public final Reference<?, ?> getReference() {
    return reference;
  }

  public final String getName() {
    return name;
  }

  public final boolean isReferred() {
    return referred;
  }

}
