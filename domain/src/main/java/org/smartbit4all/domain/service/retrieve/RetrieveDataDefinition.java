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
