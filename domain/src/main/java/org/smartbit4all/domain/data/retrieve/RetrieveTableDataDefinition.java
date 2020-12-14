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
package org.smartbit4all.domain.data.retrieve;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.domain.data.DataReference;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.retrieve.RetrieveDataDefinitionImpl;
import org.smartbit4all.domain.service.retrieve.RetrieveExecutionNode;

public class RetrieveTableDataDefinition extends RetrieveDataDefinitionImpl {

  /**
   * The table data that defines the structure of the request.
   */
  TableData<?> tableData;

  /**
   * Constructs a new definition based on a {@link TableData}.
   * 
   * @param tableData
   */
  public RetrieveTableDataDefinition(TableData<?> tableData) {
    this.tableData = tableData;
  }


  @Override
  public RetrieveExecutionNode build() {
    RetrieveExecutionNodeTableData result = new RetrieveExecutionNodeTableData(tableData, true);
    HashMap<EntityDefinition, RetrieveExecutionNodeTableData> nodesByEntity = new HashMap<>();
    // Add this node as the first one.
    nodesByEntity.put(tableData.entity(), result);
    constructExecutionNodes(result, nodesByEntity);
    return result;
  }

  /**
   * The recursive function to setup the execution plan.
   * 
   * @param currentNode The currently processed node.
   * @param nodesByEntity The registry for the already touched entities.
   */
  void constructExecutionNodes(RetrieveExecutionNodeTableData currentNode,
      Map<EntityDefinition, RetrieveExecutionNodeTableData> nodesByEntity) {
    for (DataReference<?, ?> dataReference : currentNode.tableData.referenceTo()) {
      EntityDefinition entity = dataReference.target().entity();
      RetrieveExecutionNodeTableData nextNode = nodesByEntity.get(entity);
      if (nextNode == null) {
        // We hasn't touched this entity earlier so we create a new node that doesn't need to be a
        // root.
        nextNode = new RetrieveExecutionNodeTableData(dataReference.target(), false);
        nodesByEntity.put(entity, nextNode);
        constructExecutionNodes(nextNode, nodesByEntity);
      } else {
        // In this case we reached an already existing node. So we have to set the root. From this
        // point we need and individual query for this node because of the cyclic reference.
        nextNode.setRoot(true);
      }
      // Connect the nextNode with an edge that describes the relation. This is a forward edge
      // referencing the new node with foreign keys.
      RetrieveExecutionEdgeTableData edge =
          new RetrieveExecutionEdgeTableData(nextNode, dataReference, true);
      currentNode.edges().add(edge);
    }
    // Now we follow the references through the other direction.
    for (DataReference<?, ?> dataReference : currentNode.tableData.referredBy()) {
      EntityDefinition entity = dataReference.source().entity();
      RetrieveExecutionNodeTableData nextNode = nodesByEntity.get(entity);
      if (nextNode == null) {
        // We hasn't touched this entity earlier but this is a detail so it must be a root by
        // definition.
        nextNode = new RetrieveExecutionNodeTableData(dataReference.target(), true);
        nodesByEntity.put(entity, nextNode);
        // Do the recursion.
        constructExecutionNodes(nextNode, nodesByEntity);
      } else {
        // In this case we reached an already existing node. So we have to set the root.
        nextNode.setRoot(true);
      }
      // Connect the nextNode with an edge that describes the relation.
      RetrieveExecutionEdgeTableData edge =
          new RetrieveExecutionEdgeTableData(nextNode, dataReference, false);
      currentNode.edges().add(edge);
    }
  }


  @Override
  protected void mergeNewRecords(RetrieveExecutionNode node) {

  }

}
