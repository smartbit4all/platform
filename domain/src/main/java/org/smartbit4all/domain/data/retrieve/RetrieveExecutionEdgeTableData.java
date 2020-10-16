package org.smartbit4all.domain.data.retrieve;

import org.smartbit4all.domain.data.DataReference;
import org.smartbit4all.domain.service.retrieve.RetrieveExecutionEdge;

/**
 * This edge connects two node of the retrieval execution. It points to a node that is triggered and
 * contains the {@link DataReference} and the direction. If we trigger by the source or by the
 * target.
 * 
 * @author Peter Boros
 *
 */
public class RetrieveExecutionEdgeTableData extends RetrieveExecutionEdge {

  /**
   * The data reference for accessing and mutating the instances of the references.
   */
  DataReference<?, ?> dataReference;

  RetrieveExecutionEdgeTableData(RetrieveExecutionNodeTableData node,
      DataReference<?, ?> dataReference, boolean forward) {
    super(node, dataReference.getReferenceDef(), forward);
    this.dataReference = dataReference;
  }

}
