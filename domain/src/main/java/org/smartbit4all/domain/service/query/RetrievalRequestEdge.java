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
package org.smartbit4all.domain.service.query;

import org.smartbit4all.domain.meta.AssociationDefinition;
import org.smartbit4all.domain.meta.AssociationKind;
import org.smartbit4all.domain.meta.AssociationRole;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.Reference;

/**
 * The edge is the relation among the nodes. The edge will appear in related nodes where we can set
 * if it's navigable in the given direction or not.
 * 
 * @author Peter Boros
 *
 */
public final class RetrievalRequestEdge<E extends EntityDefinition> {

  /**
   * The referred node that is pointed by the edge. The starting node contains this edge in a list
   * {@link RetrieveExecutionNode#edges}.
   */
  final RetrievalRequestNode<E> node;

  /**
   * The {@link AssociationRole} and its {@link AssociationRole#getAssociation()} association
   * provide all the necessary meta to construct the navigation query for the retrieval. There can
   * be different kind of implementation behind the {@link AssociationDefinition}.
   * 
   * <ul>
   * <li>{@link AssociationKind#REFERENCE} - The detail points to the master with a foreign key. In
   * this case if the association have any property then this properties must be stored also in the
   * detail.</li>
   * <li>{@link AssociationKind#ASSOCIATIONENTITY} - The association is an individually stored
   * association with references to the related entities. The properties of the association are
   * stored in this entity.</li>
   * </ul>
   * 
   * The {@link Reference} that defines the edge. The references show the join conditions so we can
   * use them for constructing the additional conditions for the Queries of the nodes. WARNING!!! At
   * this time we can use only one {@link Property} for the join. If we have a complex
   * {@link Reference} then the retrieval throws an exception.
   */
  final AssociationRole navigationRole;

  /**
   * The constructor appends this edge and setup the {@link #node} by adding an IN expression to the
   * {@link RetrievalRequestNode} query.
   * 
   * @param node
   * @param reference
   */
  public RetrievalRequestEdge(RetrievalRequestNode<E> node, AssociationRole navigationRole) {
    super();
    this.node = node;
    this.navigationRole = navigationRole;
  }

  final RetrievalRequestNode<E> getNode() {
    return node;
  }

  public final AssociationRole getNavigationRole() {
    return navigationRole;
  }

}
