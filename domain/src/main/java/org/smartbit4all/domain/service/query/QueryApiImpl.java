package org.smartbit4all.domain.service.query;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.smartbit4all.core.SB4CompositeFunction;
import org.smartbit4all.core.SB4CompositeFunctionImpl;
import org.smartbit4all.core.SB4Function;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.ExpressionExists;
import org.smartbit4all.domain.meta.ExpressionIn;
import org.smartbit4all.domain.meta.ExpressionReplacer;
import org.smartbit4all.domain.meta.ExpressionVisitor;
import org.smartbit4all.domain.meta.OperandProperty;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.service.dataset.DataSetApi;
import org.smartbit4all.domain.service.dataset.SaveInValues;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.domain.utility.crud.CrudRead;
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

  @Autowired(required = false)
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
        // 2. Contribute exists.
        // TODO avoid initialization if not necessary.
        Map<Expression, Expression> replacementMap = new HashMap<>();
        if (where != null) {
          where.accept(new ExpressionVisitor() {
            @Override
            public <T> void visitIn(ExpressionIn<T> expression) {
              if (expression.values() != null && expression.values().size() > 10) {
                if (expression.getOperand() instanceof OperandProperty<?>) {
                  queryNode.preCalls().call(new SaveInValues(dataSetApi, where, expression));
                }
              }
            }

            @Override
            public void visitExists(ExpressionExists expression) {
              Expression translatedReferredExpression =
                  expression.getTranslatedReferredExpression();
              if (translatedReferredExpression != null) {
                // It's an exists related to a simple referred entity. We can translate the
                // expression and attach it to the original where instead of the exists itself.
                replacementMap.put(expression, translatedReferredExpression);
              } else if (expression.getMasterReferencePath() != null) {
                EntityDefinition contextEntity = expression.getContextEntity();
                CrudRead<EntityDefinition> preQueryRead = Crud.read(contextEntity);
                // Add the property to query. We can use the one and only one join property. If we
                // have more joins then it fails.
                Reference<?, ?> lastReference = expression.getMasterReferencePath().last();
                if (lastReference.joins().isEmpty()) {
                  throw new InvalidParameterException("Missing join for " + lastReference
                      + ", unable to select for this exists criteria (" + expression + ")");
                }
                if (lastReference.joins().size() > 1) {
                  throw new UnsupportedOperationException(
                      "The exists is not supporting more then one join. (" + lastReference + ", "
                          + expression + ")");
                }

                Property<?> lastForeignKey = lastReference.joins().get(0).getSourceProperty();
                List<Reference<?, ?>> longestPath =
                    expression.getMasterReferencePath().getReferencesWithoutLast();
                String lastForeignKeyQualifiedName =
                    PropertyRef.constructName(longestPath, lastForeignKey);

                Property<?> lastForeignKeyRef =
                    contextEntity.getProperty(lastForeignKeyQualifiedName);

                preQueryRead.select(lastForeignKeyRef);

                // We have the base expression for the context entity.
                Expression contextExpression = expression.getExpression();

                // Find the conjunctive clause
                // Expression conjunctiveClause =
                // ExpressionFinder.findLargestConjunctiveClause(where, expression);

                // contextExpression.AND(where.)


                preQueryRead.where(contextExpression);

                QueryAndSaveResultAsDataSet preQuery =
                    new QueryAndSaveResultAsDataSet(dataSetApi, where, expression);

                preQuery.setInput(preQueryRead.getQuery());

                queryNode.preCalls().call(preQuery);
              }
            }
          });
        }
        for (Entry<Expression, Expression> replacementEntry : replacementMap.entrySet()) {
          ExpressionReplacer.replace(where, replacementEntry.getKey(), replacementEntry.getValue());
        }

        result.getStartingNodes().add(queryNode);
      }
    }
    return result;
  }

  @Override
  public QueryResult execute(QueryExecutionPlan execPlan) {
    QueryResult result = new QueryResult();
    for (SB4CompositeFunction<?, ?> node : execPlan.getStartingNodes()) {
      try {
        node.execute();
        for (SB4Function<?, ?> func : node.functions()) {
          if (func instanceof Query<?>) {
            result.getResultsInner().add((Query<?>) func);
          }
        }
      } catch (Exception e) {
        // TODO Add the exception as result!!!
        e.printStackTrace();
      }

    }
    return result;
  }

  @Override
  public Retrieval prepare(RetrievalRequest request) {
    // TODO Auto-generated method stub
    return null;
  }

}
