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

import org.smartbit4all.core.SB4Function;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;

/**
 * This service is responsible for the query of related entities in a recursive way. The data of the
 * entities form a graph where the nodes are the {@link RetrieveExecutionNode} and the relations are
 * the {@link RetrieveExecutionEdge}. In one step all the possible nodes are queried. It doesn't
 * belong to one entity, it's a global service.
 * 
 * @author Peter Boros
 */
public interface Retrieve extends SB4Function<RetrieveDataDefinition, RetrieveDataDefinition> {

  /**
   * We can set the data definition to the given retrieve.
   * 
   * @param dataDef The data definition must refer to {@link EntityDefinition}s and their
   *        {@link Property}s. They can be discovered by a graph traversal to identify the series of
   *        queries to be executed. This data is the result at the same time.
   * @return Fluid builder API.
   */
  Retrieve data(RetrieveDataDefinition dataDef);

  /**
   * The where for the {@link Retrieve} is an {@link Expression} that defines the starting records
   * of the root {@link EntityDefinition}.
   * 
   * @param rootFilter
   * @return
   */
  Retrieve where(Expression rootFilter);

}
