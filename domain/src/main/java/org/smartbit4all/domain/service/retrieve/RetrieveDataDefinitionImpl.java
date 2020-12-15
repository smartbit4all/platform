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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.core.SB4CompositeFunction;
import org.smartbit4all.domain.service.query.Query;

/**
 * The meta level implementation of the retrieval definition. The examination of the query cycles
 * can be implemented at this level. There is no need to know the technology of the queries. Here we
 * analyze only the results.
 * 
 * @author Peter Boros
 */
public abstract class RetrieveDataDefinitionImpl implements RetrieveDataDefinition {

  RetrieveExecutionNode rootNode;

  @Override
  public abstract RetrieveExecutionNode build();

  @Override
  public SB4CompositeFunction<RetrieveExecutionNode, RetrieveExecutionNode> processResult(
      SB4CompositeFunction<RetrieveExecutionNode, RetrieveExecutionNode> previousRound) {
    RetrieveExecutionNode node = previousRound.output();
    Map<RetrieveExecutionNode, Query> nodeQueries = new HashMap<>();

    // TODO TBC

    return null;
  }

  /**
   * This is the data representation specific merge for the new records. The new records can have
   * some references with the old ones and these references must be filled.
   * 
   * @param node
   */
  protected abstract void mergeNewRecords(RetrieveExecutionNode node);

  /**
   * The new records must be merged into the current data structure. The references must be filled
   * to have all the reference we need.
   * 
   * The next step is to find if there is more records to query by the new records. Every incoming
   * and outgoing reference must be examined.
   * 
   * @param processedEdges
   */
  private final void processNode(RetrieveExecutionNode node,
      Set<RetrieveExecutionEdge> processedEdges) {
    mergeNewRecords(node);

  }

}
