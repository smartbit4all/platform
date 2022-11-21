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
import java.util.Map;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.service.CrudApi;

/**
 * The retrieval is an Api object responsible for describing a set of related queries. The
 * {@link CrudApi} can process simple queries but also {@link RetrievalRequest}s. A Retrieval
 * Request is a set of queries with meta information to connect them. The {@link RetrievalRequest}
 * is analyzed by the {@link CrudApi} and an execution plan is produced. This
 * {@link QueryExecutionPlan} can be executed.
 * 
 * To provide meta information we can use the {@link EntityDefinition}s but even without any
 * {@link Reference} we can connect the queries by providing the meta for the joins. The queries and
 * the navigations will be used by the {@link CrudApi} to produce {@link QueryExecutionPlan}s.
 * After executing this plan the result will be analyzed to produce the next round of queries.
 * Because there is a given optimal order in the directed graph to produce all the query results we
 * need.
 * 
 * The execution of the retrieve operation is based on the hierarchy of this execution. There can be
 * different kind of retrieval mechanisms. The main idea behind is that the retrieval itself is a
 * recursive algorithm and the structure of the retrieval node graph must match. The recursion
 * always start from a root node. The graph is directed and every {@link Reference} is one edge.
 * This node-edge structure is instantiated for every Retrieve execution.
 * 
 * There are forwarding edges from the root node that form an acyclic graph. These edges can
 * potentially joins the directed nodes to the current retrieval step. But if touch an already
 * existing node then this will tear down this part from the original step. It is because we have to
 * recurse on it and we need to run this separate from the original step. It can be a backward edge
 * or an edge that reach an entity with the another path.
 * 
 * I we have a forwarding edge that points to an already reached entity then this entity must start
 * a new individual step.
 * 
 * <pre>
 *   (E1) --> E2 --> (E3) --> E4
 *     |               ^
 *     |               |
 *     |            ---    
 *     |           |
 *     --> E5 --> E6
 * </pre>
 * 
 * Here we need to query the E3 separately and it can be triggered by E2 or E6 records.
 * 
 * Also if we have an edge that points backward to an already touched entity in this path. We need
 * to execute this portion of the graph individually.
 * 
 * <pre>
 *                   ----------------
 *                   |              |
 *                   |              ^
 *   (E1) --> E2 -->(E3)--> E4 --> E7
 *                   |                    
 *                   --> E5 --> E6
 * </pre>
 * 
 * In this situation the E3 node will for a separated node it can be triggered by selecting E2 nodes
 * that refers to the records of E3 or we can have E7 records that refers E3 also.
 * 
 * If the shape of the graph allows a node (an entity gateway) to join then it can still decide to
 * skip join and run the retrieval individually.
 * 
 * The second part of the structure is the backward references, the details. In this cases the join
 * is not imaginable because these references are "on to many" directions. So we need to run them
 * individually by definition. If we already touched the given entity then it must be already a
 * separated step because of the backward reference. If it's a brand new entity then we start
 * another retrieval root node with it.
 * 
 * <pre>
 *   (E1) --> E2 --> (E3) --> E4
 *     |               ^       ^
 *     |               |       |
 *     |            ---        ---- (E7) --> E8 --> E9
 *     |           |
 *     --> E5 --> E6
 * </pre>
 * 
 * The E7 is triggered by the records of E4 but the query can produce more than one record for every
 * E4 record. But the method is not different then at E3 only the merge. In the merge phase we have
 * to know the direction of the reference and follow it. (It doesn't matter if we refer to the
 * target or we are referred by the source)
 * 
 * The execution is based on the graph. We start with the root step run and merge the result into
 * the related data tables. We collect the new record and identify the new execution steps to run.
 * If there is at least one neighbor step to triggered by the new records then we must continue with
 * these steps. From the references we know the foreign keys and the primary keys and by the new
 * records we define the IN conditions (the list of IDs typically) for the new steps. And we can run
 * the next round and so on. The recursion will end when we find no new records or we can setup a
 * limit to avoid infinite loop.
 * 
 * @author Peter Boros
 */
public class RetrievalRequest {

  /**
   * The nodes of the retrieval request where every node contains a simple QueryRequest. They must
   * be named uniquely in this context. We can add new queries named manually but without naming the
   * Retrieval will name the query.
   */
  Map<String, RetrievalRequestNode> nodes = new HashMap<>();

  public RetrievalRequestNode node(QueryInput queryInput, Expression startCondition) {
    RetrievalRequestNode result = new RetrievalRequestNode(this, queryInput, startCondition);
    nodes.put(queryInput.getName(), result);
    return result;
  }

  public RetrievalRequestNode node(QueryInput queryInput) {
    return node(queryInput, null);
  }
}
