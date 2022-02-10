package org.smartbit4all.domain.service.query;

import java.net.URI;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.smartbit4all.core.SB4CompositeFunction;
import org.smartbit4all.core.SB4CompositeFunctionImpl;
import org.smartbit4all.core.SB4Function;
import org.smartbit4all.domain.data.TableDatas;
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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * The {@link QueryApi} implementation. It analyzes the incoming query requests to produce a
 * {@link QueryExecutionPlan}. This plan can be executed later on by the
 * {@link #execute(QueryExecutionPlan)} function.
 * 
 */
@Service
public class QueryApiImpl implements QueryApi {

  private Map<String, QueryExecutionApi> executionApisByName;
  private QueryExecutorConfig config;
  protected DataSetApi dataSetApi;

  public QueryApiImpl(ApplicationContext appContext,
      @Autowired(required = false) QueryExecutorConfig config,
      @Autowired(required = false) DataSetApi dataSetApi) {
    this.executionApisByName = appContext.getBeansOfType(QueryExecutionApi.class);
    this.config = config;
    this.dataSetApi = dataSetApi;
  }

  @Override
  public QueryExecutionPlan prepare(QueryInput... queries) {
    if (queries == null || queries.length == 0) {
      return QueryExecutionPlan.EMPTY;
    }
    // Temporary solution to use a default path for the local in memory queries
    QueryExecutionPlan result = new QueryExecutionPlan("local");
    for (int i = 0; i < queries.length; i++) {
      QueryInput query = queries[i];
      if (query != null) {
        // The result is always a composite function with the give query.
        SB4CompositeFunctionImpl<QueryInput, QueryOutput> queryNode =
            new SB4CompositeFunctionImpl<>();
        queryNode.call(Queries.asFunction(query, this::executeWithoutPrepare));

        // Prepare for analyzing the query input.
        Expression where = query.where();
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
          if (func instanceof Queries.QueryFunction) {
            result.getResultsInner().add(((Queries.QueryFunction) func).output());
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
    Retrieval retrieval = new Retrieval(request);
    return retrieval;
  }

  @Override
  public QueryOutput execute(QueryInput queryInput) throws Exception {
    QueryExecutionPlan executionPlan = prepare(queryInput);
    QueryResult queryResult = execute(executionPlan);
    List<QueryOutput> results = queryResult.getResults();
    if (results == null || results.isEmpty()) {
      // FIXME is null an acceptable return value here?
      return null;
    }

    return results.stream()
        .filter(r -> queryInput.getName().equals(r.getName()))
        .findAny()
        .orElse(null);
  }

  private QueryOutput executeWithoutPrepare(QueryInput queryInput) throws Exception {
    return getExecutionApiForEntityDef(queryInput.entityDef).execute(queryInput);
  }

  private QueryExecutionApi getExecutionApiForEntityDef(EntityDefinition entityDef) {
    if (config == null) {
      if (executionApisByName.size() != 1) {
        throw new IllegalStateException(
            "No QueryExecutorConfig configured. In this case, there must be only one QueryExecutionApi registered, but there is: "
                + executionApisByName.size());
      }
      return executionApisByName.values().iterator().next();
    }

    QueryExecutionApi execApi = fromConfigExecApisByDomain(entityDef)
        .orElseGet(() -> fromConfigExecApiNamesByDomain(entityDef)
            .orElseGet(() -> fromConfigExecApisByEntityUri(entityDef)
                .orElseGet(() -> fromConfigExecApiNamesByEntityUri(entityDef)
                    .orElse(null))));

    if (execApi == null) {
      throw new IllegalStateException(
          "There is no QueryExecutionApi configured for EntityDefinition: "
              + entityDef.entityDefName());
    }

    return execApi;
  }

  private Optional<QueryExecutionApi> fromConfigExecApiNamesByDomain(EntityDefinition entityDef) {
    return config.execApiNamesByDomain.entrySet().stream()
        .filter(es -> es.getKey().equals(entityDef.getDomain()))
        .findAny()
        .map(es -> executionApisByName.get(es.getValue()));
  }

  private Optional<QueryExecutionApi> fromConfigExecApiNamesByEntityUri(
      EntityDefinition entityDef) {
    return config.execApiNamesByEntityUri.entrySet().stream()
        .filter(es -> es.getKey().equals(entityDef.getUri()))
        .findAny()
        .map(es -> executionApisByName.get(es.getValue()));
  }

  private Optional<QueryExecutionApi> fromConfigExecApisByEntityUri(EntityDefinition entityDef) {
    return config.execApisByEntityUri.entrySet().stream()
        .filter(es -> es.getKey().equals(entityDef.getUri()))
        .findAny()
        .map(es -> es.getValue());
  }

  private Optional<QueryExecutionApi> fromConfigExecApisByDomain(EntityDefinition entityDef) {
    return config.execApisByDomain.entrySet().stream()
        .filter(es -> es.getKey().equals(entityDef.getDomain()))
        .findAny()
        .map(es -> es.getValue());
  }

  public static class QueryExecutorConfig {
    /*
     * TODO later on (when the usage of this config is clear by the actual usage) a builder can be
     * implemented here
     *
     * For now these 4 maps are just first attempts to create a config for the
     * EntityDef-QueryExecutionApi meta informations
     */
    Map<URI, QueryExecutionApi> execApisByEntityUri;
    Map<String, QueryExecutionApi> execApisByDomain;
    Map<URI, String> execApiNamesByEntityUri;
    Map<String, String> execApiNamesByDomain;

    private QueryExecutorConfig() {
      execApisByEntityUri = new HashMap<>();
      execApisByDomain = new HashMap<>();
      execApiNamesByEntityUri = new HashMap<>();
      execApiNamesByDomain = new HashMap<>();
    }

    public static QueryExecutorConfig create() {
      return new QueryExecutorConfig();
    }

    public QueryExecutorConfig addExecutionApiForEntityUri(URI entityUri,
        QueryExecutionApi executionApi) {
      execApisByEntityUri.put(entityUri, executionApi);
      return this;
    }

    public QueryExecutorConfig addExecutionApiForDomain(String domainName,
        QueryExecutionApi executionApi) {
      execApisByDomain.put(domainName, executionApi);
      return this;
    }

    public QueryExecutorConfig addExecutionApiForEntityUri(URI entityUri, String executionApiName) {
      execApiNamesByEntityUri.put(entityUri, executionApiName);
      return this;
    }

    public QueryExecutorConfig addExecutionApiForEntityUri(String domainName,
        String executionApiName) {
      execApiNamesByDomain.put(domainName, executionApiName);
      return this;
    }

  }

  @Override
  public Retrieval execute(Retrieval retrieval) throws Exception {
    RetrievalRound round = retrieval.next();
    while (!round.queries.isEmpty()) {
      round.queries.entrySet().parallelStream().forEach(e -> {
        try {
          QueryOutput queryOutput = execute(e.getValue());
          if (queryOutput.isResultSerialized()) {
            // TODO this case should be also handled during a retrieval process!
            throw new IllegalStateException(
                "QueryOutput can not have serialized result in a Retrieval!");
          }
          if (e.getKey().getResult() == null) {
            e.getKey().setResult(queryOutput.getTableData());
            e.getKey().getNewRows().addAll(e.getKey().getResult().rows());
          } else {
            e.getKey().getNewRows()
                .addAll(TableDatas.append(e.getKey().getResult(), queryOutput.getTableData()));
          }
          e.getKey().incrementQueryCount();
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      });
      round = retrieval.next();
    }
    return retrieval;
  }

  @Override
  public String getSchema(EntityDefinition entityDef) {
    QueryExecutionApi executionApiForEntityDef = getExecutionApiForEntityDef(entityDef);
    return executionApiForEntityDef.getSchema();
  }
}
