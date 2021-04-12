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
package org.smartbit4all.api.query;

import org.smartbit4all.domain.service.query.Query;

/**
 * This API is responsible for executing query against the current application setup. In a simple
 * application there can a be an SQL level implementation. But in a complex application there can be
 * special contributions for executing a query object.
 * 
 * @author Peter Boros
 */
public interface QueryApi {

  /**
   * Process the queries in the parameter to prepare for the execution. As a result it modifies the
   * queries and include them into an {@link QueryExecutionPlan}.
   * 
   * @param queries
   * @return
   */
  QueryExecutionPlan prepare(Query<?>... queries);

  /**
   * Executes the plan in the current application context.
   * 
   * @param execPlan
   */
  QueryResult execute(QueryExecutionPlan execPlan);

}
