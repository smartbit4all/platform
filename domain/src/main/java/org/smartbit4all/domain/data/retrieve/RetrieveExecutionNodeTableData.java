package org.smartbit4all.domain.data.retrieve;

import org.smartbit4all.domain.data.DataColumnOwned;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.service.query.Query;
import org.smartbit4all.domain.service.retrieve.RetrieveExecutionNode;

/**
 * The execution of the retrieve operation is based on the hierarchy of this execution. There can be
 * different kind of retrieval mechanisms. The main idea behind is that the retrieval itself is a
 * recursive algorithm and the structure of the retrieval node graph must match. The recursion
 * always start from a root node. The graph is directed and every {@link Reference} is one edge.
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
public class RetrieveExecutionNodeTableData extends RetrieveExecutionNode {

  /**
   * The data table belong to the given node.
   */
  TableData<?> tableData;

  /**
   * Constructs the plan based on the data table.
   * 
   * @param tableData The data table to start from.
   */
  RetrieveExecutionNodeTableData(TableData<?> tableData, boolean root) {
    super(root);
    this.tableData = tableData;
  }

  @Override
  public Query constructQuery(Expression filter) {
    // The start with filter defines the first query based on the root TableData we have in this
    // node.
    Query query = entity().services().crud().query();

    // The naive algorithm query always the owned columns of the TableData. We believe that the
    // referred columns will be queried by nodes next to us.
    query.select(tableData.properties(c -> c instanceof DataColumnOwned<?>)).where(filter)
        .into(tableData);
    return query;
  }

  @Override
  public EntityDefinition entity() {
    return tableData.entity();
  }

}
