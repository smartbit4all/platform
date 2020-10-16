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
