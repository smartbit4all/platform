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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.meta.AssociationDefinition;
import org.smartbit4all.domain.meta.AssociationKind;
import org.smartbit4all.domain.meta.AssociationRole;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Reference;
import org.springframework.util.Assert;

/**
 * This object instantiated for the execution of the {@link RetrievalRequest} by the
 * {@link QueryApi}. It analyze the Queries we have and define the final structure of the retrieval.
 * It contains the input information and the meta to define the rounds and analyze their result. The
 * starting nodes for the first round of the {@link Retrieval} is identified by
 * {@link RetrievalRequestNode#getStartCondition()}. If it's not empty then we can start the
 * retrieval with this node.
 * 
 * After the first round we can have newly queried row at given nodes. These nodes are the starting
 * nodes for the next round. The outgoing navigable edges will define the join conditions of the new
 * round.
 * 
 * @author Peter Boros
 *
 */
public final class Retrieval {

  /**
   * The original request of the retrieval.
   */
  private final RetrievalRequest originalRequest;

  public Retrieval(RetrievalRequest originalRequest) {
    super();
    this.originalRequest = originalRequest;
  }

  /**
   * The nodes of the retrieval.
   */
  Map<String, RetrievalNode<?>> nodes = new HashMap<>();

  /**
   * Based on the current state of the retrieval this function calculates the parallel executable
   * queries of the next round.
   * 
   * @return
   */
  RetrievalRound next() {
    List<RetrievalNode<?>> startingNodes = nodes.values().stream()
        .filter(RetrievalNode::readyForNextRound).collect(Collectors.toList());
    Map<RetrievalNode<?>, QueryInput> nextRoundQueries = new HashMap<>();
    for (RetrievalNode<?> node : startingNodes) {
      for (@SuppressWarnings("rawtypes")
      RetrievalEdge edge : node.getNavigableEdges()) {
        QueryInput query = nextRoundQueries.computeIfAbsent(edge.getNode(),
            n -> Queries.copy(n.getRequestNode().getQuery()));
        List<DataRow> newRows = node.getNewRows();
        if (!newRows.isEmpty()) {
          // We can add the IN ( NEWROWS ) condition to the query.
          // edge.getRequestEdge().getReference().joins().stream()
          // .map(j -> node.getResult().getColumn(j.getSourceProperty()))
          // .collect(Collectors.toList());

          AssociationRole associationRole = edge.getRequestEdge().getNavigationRole();
          AssociationDefinition association = associationRole.getAssociation();
          if (association.getKind() == AssociationKind.REFERENCE) {
            Assert.isTrue(association.getRoles().size() == 2,
                "Association with REFERENCE kind must have exactly two ends.");
            // In this case we have a reference on the detail side of the association. There is no
            // association entity so the join is simple.
            Reference<?, ?> reference = associationRole.getReference();
            boolean detail = true;
            if (reference == null) {
              // We need the reference of the current node to the edge.
              AssociationRole referenceRole = null;
              for (AssociationRole role : association.getRoles().values()) {
                if (role != associationRole) {
                  referenceRole = role;
                  break;
                }
              }
              Assert.notNull(referenceRole,
                  "The reference role must exist in the " + association);
              reference = referenceRole.getReference();
              detail = false;
            }
            // We must add the join conditions to the node query. It will use the new rows as
            // parameter for in the in. Construct the condition based on the reference.
            Expression joinExpression =
                detail ? reference.joinDetail(newRows) : reference.joinMaster(newRows);
          } else if (association.getKind() == AssociationKind.ASSOCIATIONENTITY) {
            // The two reference of the association entity is going to be used to have a detail
            // condition and a join between the association entity and the navigated entity.
            EntityDefinition associationEntity = association.getAssociationEntity();
            Assert.notNull(associationEntity,
                "The association entity of " + association + " must be set.");
            // This refers to the navigable entity. The query must be modified to start on the
            // association entity and all the selected properties must be joined.
            Reference<?, ?> reference = associationRole.getReference();
            // The translated query to run against the association entity to retrieve the related
            // records in one query.
            // TODO The prepare will modify the original retrieve to have the proper queries
            QueryInput translatedQuery = Queries.copyTranslated(query, associationEntity, null);
          }
          newRows.clear();
          // query.where(Expression.AND(query.input().where(), edge.getRequestEdge().));
        } else if (node.getRequestNode().getStartCondition() != null) {
          // In this case we can use the starting condition for the current query.

        }
      }
    }
    return new RetrievalRound();
  }

}
