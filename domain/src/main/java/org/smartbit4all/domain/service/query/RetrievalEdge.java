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

import org.smartbit4all.domain.service.CrudApi;

/**
 * The retrieval edge is responsible for the relation between two {@link RetrievalNode}. It's used
 * as a register to manage and store the references among between the rows of the two related node.
 * The retrieval is started at the root nodes identified by the root conditions. But after the first
 * round the navigation is calculated by the navigable edges of the already fetched nodes. In every
 * round the {@link CrudApi} identifies the nodes to query. The input of the navigation is the
 * newly queried rows of the nodes. If a node has new rows then the navigable outgoing edges are
 * evaluated and defines the queries of the next round.
 * 
 * @author Peter Boros
 *
 */
public class RetrievalEdge {

  private final RetrievalRequestEdge requestEdge;

  private final RetrievalNode node;

  public RetrievalEdge(RetrievalRequestEdge requestEdge, RetrievalNode node) {
    super();
    this.requestEdge = requestEdge;
    this.node = node;
  }

  final RetrievalRequestEdge getRequestEdge() {
    return requestEdge;
  }

  final RetrievalNode getNode() {
    return node;
  }


}
