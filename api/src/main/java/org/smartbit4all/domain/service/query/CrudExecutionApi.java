package org.smartbit4all.domain.service.query;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.CrudApi;
import org.smartbit4all.domain.service.modify.CreateInput;
import org.smartbit4all.domain.service.modify.CreateOutput;
import org.smartbit4all.domain.service.modify.DeleteInput;
import org.smartbit4all.domain.service.modify.DeleteOutput;
import org.smartbit4all.domain.service.modify.UpdateInput;
import org.smartbit4all.domain.service.modify.UpdateOutput;

/**
 * This api is responsible for executing a QueryRequest object against a specific technology and
 * data store. This API can be parameterized to the {@link EntityDefinition} but can be overridden
 * in the Query definition also. The {@link CrudApi} will collect all the available
 * {@link CrudExecutionApi}s to serve the complex query requests. In a given query the
 * {@link EntityDefinition}s are bound to the {@link CrudExecutionApi}s and this binding is used to
 * produce the {@link QueryExecutionPlan}.
 * 
 * @author Peter Boros
 */
public interface CrudExecutionApi {

  /**
   * Executes a query
   * 
   * @param <E> The entity definition parameters.
   * @param queryInput The query object.
   * @return The output of the query.
   */
  <E extends EntityDefinition> QueryOutput executeQuery(QueryInput queryInput);

  /**
   * Executes a create operation.
   * 
   * @param <E> The entity definition parameters.
   * @param input The input object of the create operation.
   * @return The output of the operation.
   */
  <E extends EntityDefinition> CreateOutput executeCreate(CreateInput<E> input);

  /**
   * Executes an update operation.
   * 
   * @param <E> The entity definition parameters.
   * @param input The input object of the update operation.
   * @return The output of the operation.
   */
  <E extends EntityDefinition> UpdateOutput executeUpdate(UpdateInput<E> input);

  /**
   * Executes a delete operation.
   * 
   * @param <E> The entity definition parameters.
   * @param input The input object of the delete operation.
   * @return The output of the operation.
   */
  <E extends EntityDefinition> DeleteOutput executeDelete(DeleteInput<E> input);

  String getSchema();

}
