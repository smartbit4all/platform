package org.smartbit4all.domain.service.query;

import org.smartbit4all.domain.meta.EntityDefinition;

/**
 * This api is responsible for executing a {@link Query} object against a specific technology and
 * data store. This API can be parameterized to the {@link EntityDefinition} but can be overridden
 * in the Query definition also. The {@link QueryApi} will collect all the available
 * {@link QueryExecutionApi}s to serve the complex query requests. In a given query the
 * {@link EntityDefinition}s are bound to the {@link QueryExecutionApi}s and this binding is used to
 * produce the {@link QueryExecutionPlan}.
 * 
 * @author Peter Boros
 */
public interface QueryExecutionApi {

  /**
   * Executes a query
   * 
   * @param <E> The entity definition parameters.
   * @param queryRequest The query object.
   * @return The output of the query.
   */
  <E extends EntityDefinition> QueryOutput<E> execute(Query<E> queryRequest);

}
