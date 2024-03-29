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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.domain.meta.AssociationRole;
import org.smartbit4all.domain.meta.Expression;

/**
 * The retrieval request node is a the basic building block of the {@link RetrievalRequest}. Every
 * node represents a {@link QueryInput} that can be executed individually and related with other
 * nodes. Node can produce records during the execution and these records are used as input set for
 * other queries.
 * 
 * @author Peter Boros
 *
 */
public final class RetrievalRequestNode {

  private final WeakReference<RetrievalRequest> request;

  /**
   * The query definition of the given node.
   */
  private QueryInput query;

  /**
   * If we setup a start condition for the given node then it will be used as starting node for the
   * execution of the retrieval.
   */
  private Expression startCondition = null;

  /**
   * The navigable edges of the node. These edges refers to nodes that are using the results of this
   * node. The leaf nodes of a {@link RetrievalRequest} are the ones without any outgoing edges. The
   * root nodes doesn't have any incoming edges. If there is a cycle then the root cann't be
   * identified automatically so we need to setup manually.
   */
  private Map<String, RetrievalRequestEdge> edges = new HashMap<>();

  RetrievalRequestNode(RetrievalRequest retrievalRequest, QueryInput query,
      Expression startCondition) {
    super();
    this.query = query;
    this.startCondition = startCondition;
    this.request = new WeakReference<>(retrievalRequest);
  }

  final QueryInput getQuery() {
    return query;
  }

  final void setQuery(QueryInput query) {
    this.query = query;
  }

  public final Expression getStartCondition() {
    return startCondition;
  }

  public final void setStartCondition(Expression startCondition) {
    this.startCondition = startCondition;
  }

  public final Map<String, RetrievalRequestEdge> getEdges() {
    return edges;
  }

  public final RetrievalRequestEdge edge(AssociationRole associationRole, QueryInput queryInput) {
    RetrievalRequestNode node = request.get().node(queryInput);
    return new RetrievalRequestEdge(node, associationRole);
  }

  public final RetrievalRequestEdge edge(AssociationRole associationRole,
      RetrievalRequestNode node) {
    RetrievalRequestEdge requestEdge = new RetrievalRequestEdge(node, associationRole);
    edges.put(requestEdge.getRole().getName(), requestEdge);
    return requestEdge;
  }

}
