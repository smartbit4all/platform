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
package org.smartbit4all.domain.service;

import java.util.Map;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.service.modify.CreateInput;
import org.smartbit4all.domain.service.modify.CreateOutput;
import org.smartbit4all.domain.service.modify.DeleteInput;
import org.smartbit4all.domain.service.modify.DeleteOutput;
import org.smartbit4all.domain.service.modify.UpdateInput;
import org.smartbit4all.domain.service.modify.UpdateOutput;
import org.smartbit4all.domain.service.query.CrudExecutionApi;
import org.smartbit4all.domain.service.query.QueryExecutionPlan;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.smartbit4all.domain.service.query.QueryResult;
import org.smartbit4all.domain.service.query.Retrieval;
import org.smartbit4all.domain.service.query.RetrievalRequest;

/**
 * This API is responsible for executing query, create, update and delete operations against the
 * current application setup. In a simple application there can a be an SQL level implementation.
 * But in a complex application there can be special contributions for executing a query object. A
 * single QueryRequest is executed by the {@link CrudExecutionApi} and this Api orchestrates these
 * query executions.
 * 
 * @author Peter Boros
 */
public interface CrudApi {

  /**
   * Process the queries in the parameter to prepare for the execution. As a result it modifies the
   * queries and include them into an {@link QueryExecutionPlan}.
   * 
   * @param queries
   * @return
   */
  QueryExecutionPlan prepareQueries(QueryInput... queries);

  /**
   * The RetrievalRequest is analyzed and the final {@link Retrieval} is constructed. In this we
   * have the meta information and the current state of the execution.
   * 
   * @param request The request.
   * @return The prepared Retrieval object ready to execute.
   */
  Retrieval prepareRetrieval(RetrievalRequest request);

  /**
   * Executes the plan in the current application context.
   * 
   * @param execPlan
   */
  QueryResult executeQueryPlan(QueryExecutionPlan execPlan);

  /**
   * Executes a single query by the given input
   * 
   * @param queryInput
   * @return the query output with a new TableData and a name matching the input
   */
  QueryOutput executeQuery(QueryInput queryInput);

  /**
   * Executes a single create.
   * 
   * @param <E>
   * @param input The input parameter object.
   * @return The output.
   */
  <E extends EntityDefinition> CreateOutput executeCreate(CreateInput<E> input);

  /**
   * Executes a single update.
   * 
   * @param <E>
   * @param input The input parameter object.
   * @return The output.
   */
  <E extends EntityDefinition> UpdateOutput executeUpdate(UpdateInput<E> input);

  /**
   * Executes a single delete.
   * 
   * @param <E>
   * @param input The input parameter object.
   * @return The output.
   */
  <E extends EntityDefinition> DeleteOutput executeDelete(DeleteInput<E> input);

  /**
   * Executes a single query by the given input
   * 
   * @param retrieval
   * @return the query output with a new TableData and a name matching the input
   * @throws Exception
   */
  Retrieval executeRetrieval(Retrieval retrieval) throws Exception;

  /**
   * Return the schema of the entity definition by the parameterization of the application.
   * 
   * @param entityDef
   * @return
   */
  String getSchema(EntityDefinition entityDef);

  /**
   * This service ensure the caller that the defined records exist in the database after the call
   * and locked at the same time. It's optimized in a way that the insert for a unique column means
   * lock for this value.
   * 
   * @param <T> The type of the unique property.
   * @param entityDef The {@link EntityDefinition} for the operation.
   * @param uniqueProperty The unique property.
   * @param uniqueValue The value of the unique property.
   * @param sequenceName
   * @param extraValues The additional values in the unique record to set. It will be used only if
   *        there is a new record.
   * @return The table data of the existing record. It's already lock if we get back the result.
   */
  <E extends EntityDefinition, T> TableData<E> lockOrCreateAndLock(E entityDef,
      Property<T> uniqueProperty, T uniqueValue,
      String sequenceName, Map<Property<?>, Object> extraValues) throws Exception;


  /**
   * Checks whether a {@link CrudExecutionApi} is registered for the entity definition by the
   * parameterization of the application. If there is no parameterization, then return false.
   * 
   * @param entityDef
   * @return
   */
  boolean isExecutionApiExists(EntityDefinition entityDef);
}
