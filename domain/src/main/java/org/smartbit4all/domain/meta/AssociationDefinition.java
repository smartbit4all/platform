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

import java.util.Map;
import org.smartbit4all.core.utility.ListBasedMap;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.service.query.RetrievalRequest;

/**
 * The association is relation between {@link EntityDefinition}s but at the same time it's an
 * {@link EntityDefinition} on its own. Because this definition can contain properties as well
 * because we can add properties to the association itself. But anyway this is primarily the
 * association meta defining the direction of navigation, the multiplicity and so on. If we have a
 * {@link Reference} from one EntityDefinition to another then the {@link AssociationDefinition} is
 * generated and registered automatically.
 * 
 * TODO Generate {@link AssociationDefinition} from {@link Reference} instances. In this case the
 * both direction is navigable and the multiplicity is obvious, many to one.
 * 
 * The association is N-ary by definition. It can have as many role as we need. All the roles must
 * be named uniquely in context of the association. The roles are qualified by multiplicity,
 * navigability. The multiplicity means that in an association how many instance can occur. In a
 * {@link RetrievalRequest} we can add the association as the definition of navigation and we can
 * point to one role to navigate also. The retrieval will query the root entity and the association
 * to reach the navigated entity by the role.
 * 
 * The detailed theory of association is a commonly defined in the UML standard for example.
 * 
 * @see <a href="https://www.uml-diagrams.org/association.html??context=class-diagrams">UML standard
 *      - association</a>
 * 
 * @author Peter Boros
 *
 */
public class AssociationDefinition {

  /**
   * The association can be referenced based where in the referrer object we store a pointer
   * (reference) to the referred object. In this case the multiplicity is by definition one for the
   * referred.
   * 
   * In case of {@link AssociationKind#ASSOCIATIONENTITY} we have an object to store the
   * relationship between two or more objects. In this case we can ask for the association entity
   * itself using its {@link Reference} towards the related entities. But we can skip this
   * association entity and ask only the associated objects directly.
   */
  private AssociationKind kind;

  /**
   * The roles of the association representing the edges of the association. There can two or more
   * related entities identified by a symbolic name.
   */
  private Map<String, AssociationRole> roles = new ListBasedMap<>();

  /**
   * If the association is implemented by an association entity then this is the definition.
   */
  private EntityDefinition associationEntity;

  /**
   * If we have an {@link AssociationKind#REFERENCE} association then we can access the referrer
   * directly from the association.
   */
  private AssociationRole referrer;

  /**
   * If we have an {@link AssociationKind#REFERENCE} association then we can access the referred
   * directly from the association.
   */
  private AssociationRole referred;

  /**
   * Setup a new association based on the meta from a {@link Reference}. By definition every
   * {@link Reference} is an association that can be navigated
   * 
   * @param reference
   */
  public AssociationDefinition(Reference<?, ?> reference) {
    super();
    this.kind = AssociationKind.REFERENCE;
    referred =
        new AssociationRole(reference.getName(), this, reference.getTarget(), reference,
            reference.isMandatory() ? Multiplicity.ONE : Multiplicity.ZERO_OR_ONE, true);
    // TODO Name the source direction of the reference! Use this for the role.
    referrer =
        new AssociationRole(
            reference.getName() + StringConstant.DOT
                + reference.getSource().getClass().getSimpleName(),
            this, reference.getSource(), reference,
            reference.isMandatory() ? Multiplicity.ONE : Multiplicity.ZERO_OR_ONE, false);
    roles.put(referred.getName(), referred);
    roles.put(referrer.getName(), referrer);
  }

  /**
   * @return The kind of the given association.
   */
  public AssociationKind getKind() {
    return kind;
  }

  /**
   * The map of the roles identified by the role name that must be unique in the association
   * context.
   * 
   * @return
   */
  public Map<String, AssociationRole> getRoles() {
    return roles;
  }

  /**
   * The association entity defines the properties of the association itself. If we join the
   * entities of the roles with this association then it's not necessary to define any condition for
   * the association. It can be set to define the association entity that contains
   * {@link Reference}s to the roles. And it can define the properties as well.
   * 
   * @return
   */
  public EntityDefinition getAssociationEntity() {
    return associationEntity;
  }

  public final AssociationRole getReferrer() {
    return referrer;
  }

  public final AssociationRole getReferred() {
    return referred;
  }

}
