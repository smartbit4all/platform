/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.service.retrieve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.service.query.Query;
import org.smartbit4all.domain.utility.CompositeValue;

/**
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
 * TODO It can be a technical limit or we can define in case of a reference that this is lazy. Which
 * means that the retrieval will stop there.
 * 
 * @author Peter Boros
 *
 */
public abstract class RetrieveExecutionNode {

  /**
   * Defines if the given node must be executed individually to query the records.
   */
  protected boolean root;

  /**
   * The edges to the neighboring nodes.
   */
  protected List<RetrieveExecutionEdge> edges = new ArrayList<>();

  /**
   * This map contains the indices for the referred columns. All the indices are identified by the
   * concatenated names of the properties separated by a dot. If we have more than one property in
   * an index then we use {@link CompositeValue} for the key object. Else we use directly the value
   * from the property.
   */
  protected Map<String, Map<Object, DataRow>> indices = new HashMap<>();

  /**
   * If new records are added in a round then these records will be added to the list and it will be
   * the basic of the next round. When a round process these records then it clear the records from
   * this before executing the queries and fill the list again with the new records. If a round
   * hasn't produced any new records then it's the end of the recursion.
   */
  protected List<DataRow> newRecordsToProcess = new ArrayList<>();

  /**
   * Constructs the plan based on the data table.
   * 
   */
  protected RetrieveExecutionNode(boolean root) {
    super();
    this.root = root;
  }

  public boolean isRoot() {
    return root;
  }

  public void setRoot(boolean root) {
    this.root = root;
  }

  /**
   * The edges starting from this node.
   * 
   * @return
   */
  public List<RetrieveExecutionEdge> edges() {
    return edges;
  }

  /**
   * This function creates a list of executable query by the nodes and edges of the retrieval graph.
   * It could lead to a single executable query but can produce more depending on the complexity of
   * the data structure.
   * 
   * @param filter If we have any starting filter then it can assume. Usually this expression is
   *        empty because the retrieval logic uses the references to identify the records of the
   *        next round. But with this additional filter we can control the result of the given
   *        entities.
   * @return The composite query services.
   */
  public abstract Query constructQuery(Expression filter);

  /**
   * The {@link EntityDefinition} related to this node. It will be used to access the query service
   * and the meta.
   * 
   * @return
   */
  public abstract EntityDefinition entity();

}
