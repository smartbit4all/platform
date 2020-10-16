package org.smartbit4all.domain.service.retrieve;

import org.smartbit4all.core.SB4CompositeFunction;
import org.smartbit4all.core.SB4CompositeFunctionImpl;
import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.service.query.Query;

/**
 * The abstract implementation of the
 * 
 * @author Peter Boros
 */
public abstract class RetrieveImpl
    extends SB4FunctionImpl<RetrieveDataDefinition, RetrieveDataDefinition> implements Retrieve {

  /**
   * The data definition for the {@link Retrieve}.
   */
  protected RetrieveDataDefinition dataDef;

  /**
   * The conditions
   */
  protected Expression rootFilter;

  @Override
  public Retrieve data(RetrieveDataDefinition dataDef) {
    this.dataDef = dataDef;
    return this;
  }

  @Override
  public Retrieve where(Expression rootFilter) {
    this.rootFilter = rootFilter;
    return this;
  }

  @Override
  public void execute() throws Exception {
    RetrieveExecutionNode executionNode = (RetrieveExecutionNode) dataDef.build();
    // We start with the root node and query by the rootFilter to start the recursion.
    Query rootQuery = executionNode.constructQuery(rootFilter);
    // TODO Get the proper composite service. - How to get the CompositeFunction as Spring bean.
    SB4CompositeFunction<RetrieveExecutionNode, RetrieveExecutionNode> nextRound =
        new SB4CompositeFunctionImpl<RetrieveExecutionNode, RetrieveExecutionNode>();
    nextRound.call(rootQuery);

    while (!nextRound.isEmpty()) {
      nextRound.execute();
      // Read the results and prepare the next round. We delegate this extraction to the nodes.
      nextRound = dataDef.processResult(nextRound);
    }
  }

  @Override
  public RetrieveDataDefinition input() {
    return dataDef;
  }

  @Override
  public RetrieveDataDefinition output() {
    return dataDef;
  }

}
