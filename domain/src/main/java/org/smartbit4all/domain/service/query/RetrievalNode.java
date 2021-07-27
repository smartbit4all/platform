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

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

/**
 * The Retrieval node is the execution instance of the {@link RetrievalRequestNode}. It contains all
 * the retrieved data and the edges with the actual usage of the keys. The content of already
 * retrieved data is stored in a {@link TableData} to be able to have only partial data. The data
 * itself contains every property that is necessary for the execution of the retrieval.
 * 
 * @author Peter Boros
 *
 */
public class RetrievalNode<E extends EntityDefinition> {

  /**
   * The result of the given node filled by executing the {@link Retrieval}. Can be expanded round
   * by round because the given query can result more and more rows by every query.
   */
  private TableData<E> result;

  /**
   * These are the newly queried rows for the node.
   */
  final List<DataRow> newRows = new ArrayList<>();

  /**
   * The original request node that contains the meta data for the given node.
   */
  private RetrievalRequestNode<E> requestNode;

  /**
   * These edges are outgoing references so if this node is starting node in a
   * {@link RetrievalRound} then the referred node are going to be included in the next query round.
   */
  final List<RetrievalEdge<?>> navigableEdges = new ArrayList<>();

  /**
   * The queryCount store the number of query executions on the given node.
   */
  private int queryCount = 0;

  final TableData<E> getResult() {
    return result;
  }

  final void setResult(TableData<E> result) {
    this.result = result;
  }

  final RetrievalRequestNode<E> getRequestNode() {
    return requestNode;
  }

  final void setRequestNode(RetrievalRequestNode<E> requestNode) {
    this.requestNode = requestNode;
  }

  final int getQueryCount() {
    return queryCount;
  }

  final void setQueryCount(int queryCount) {
    this.queryCount = queryCount;
  }

  /**
   * This function evaluate the current state of the node.
   * 
   * @return True if there was no query on the node but we have a not empty
   *         {@link RetrievalRequestNode#getStartCondition()}. The result is also true
   */
  final boolean readyForNextRound() {
    return (queryCount == 0 && requestNode.getStartCondition() != null) || !newRows.isEmpty();
  }

  final List<RetrievalEdge<?>> getNavigableEdges() {
    return navigableEdges;
  }

  final List<DataRow> getNewRows() {
    return newRows;
  }

}
