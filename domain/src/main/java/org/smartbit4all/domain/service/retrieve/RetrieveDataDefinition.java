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

import org.smartbit4all.core.SB4CompositeFunction;

/**
 * This interface provides the {@link RetrieveExecutionNode} and {@link RetrieveExecutionEdge} graph
 * for the algorithm. It can be used for table based structures and bean structures as well.
 * 
 * @author Peter Boros
 */
public interface RetrieveDataDefinition {

  /**
   * The build constructs the {@link RetrieveExecutionNode} and {@link RetrieveExecutionEdge} graph
   * from the data definition it has.
   * 
   * @return The constructed graph.
   */
  RetrieveExecutionNode build();

  /**
   * After a query round we analyze the result to see if there is anything else to query. We examine
   * the new records of the previous round. If we doesn't have new records or there is no more
   * relation to follow from the new records the we stop. Else we create the composite function for
   * the next round.
   * 
   * @param previousRound
   * @return
   */
  SB4CompositeFunction<RetrieveExecutionNode, RetrieveExecutionNode> processResult(
      SB4CompositeFunction<RetrieveExecutionNode, RetrieveExecutionNode> previousRound);

}
