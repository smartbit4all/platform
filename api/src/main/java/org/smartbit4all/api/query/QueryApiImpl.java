package org.smartbit4all.api.query;

import org.smartbit4all.core.SB4CompositeFunctionImpl;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.ExpressionIn;
import org.smartbit4all.domain.meta.ExpressionVisitor;
import org.smartbit4all.domain.meta.OperandProperty;
import org.smartbit4all.domain.service.dataset.DataSetApi;
import org.smartbit4all.domain.service.query.Query;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The {@link QueryApi} abstract implementation. It analyzes the incoming query requests to produce
 * a {@link QueryExecutionPlan}. This plan can be executed later on by the
 * {@link #execute(QueryExecutionPlan)} function.
 * 
 * <p>
 * TODO Near future plan: remove the SQLQuery and make the Query itself abstract. So if we have a
 * query then the query is abstract and we can decide how to execute it in the API. The SQL query is
 * a kind of contribution in the {@link QueryApi}.
 * </p>
 * 
 * @author Peter Boros
 */
public class QueryApiImpl implements QueryApi {

  @Autowired
  protected DataSetApi dataSetApi;

  @Override
  public QueryExecutionPlan prepare(Query<?>... queries) {
    if (queries == null || queries.length == 0) {
      return QueryExecutionPlan.EMPTY;
    }
    // Temporary solution to use a default path for the local in memory queries
    QueryExecutionPlan result = new QueryExecutionPlan("local");
    for (int i = 0; i < queries.length; i++) {
      Query<?> query = queries[i];
      if (query != null) {
        // The result is always a composite function with the give query.
        SB4CompositeFunctionImpl<QueryInput<?>, QueryOutput<?>> queryNode =
            new SB4CompositeFunctionImpl<>();
        queryNode.call(query);

        // Prepare for analyzing the query input.
        QueryInput<?> input = query.input();
        Expression where = input.where();
        // 1. Contribution: over sized in
        if (where != null) {
          where.accept(new ExpressionVisitor() {
            @Override
            public <T> void visitIn(ExpressionIn<T> expression) {
              if (expression.values() != null && expression.values().size() > 10) {
                if (expression.getOperand() instanceof OperandProperty<?>) {
                  OperandProperty<T> operandProperty = (OperandProperty<T>) expression.getOperand();
                  // TODO The URI structure must be defined!
                  dataSetApi.activate(operandProperty.property(), expression.values());
                }
              }
            }
          });
        }

        result.getStartingNodes().add(queryNode);
      }
    }
    return result;
  }

  @Override
  public QueryResult execute(QueryExecutionPlan execPlan) {
    // TODO Auto-generated method stub
    return null;
  }

}
