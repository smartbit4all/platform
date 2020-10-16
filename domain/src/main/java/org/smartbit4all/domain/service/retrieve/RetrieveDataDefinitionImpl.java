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
