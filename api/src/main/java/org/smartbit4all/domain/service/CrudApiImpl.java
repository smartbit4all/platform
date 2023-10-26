package org.smartbit4all.domain.service;

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
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.filtering.ExpressionEvaluationPlan;
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
import org.smartbit4all.domain.service.dataset.TableDataApi;
import org.smartbit4all.domain.service.identifier.IdentifierService;
import org.smartbit4all.domain.service.identifier.NextIdentifier;
import org.smartbit4all.domain.service.modify.CreateInput;
import org.smartbit4all.domain.service.modify.CreateOutput;
import org.smartbit4all.domain.service.modify.DeleteInput;
import org.smartbit4all.domain.service.modify.DeleteOutput;
import org.smartbit4all.domain.service.modify.UpdateInput;
import org.smartbit4all.domain.service.modify.UpdateOutput;
import org.smartbit4all.domain.service.query.CrudExecutionApi;
import org.smartbit4all.domain.service.query.Queries;
import org.smartbit4all.domain.service.query.QueryAndSaveResultAsDataSet;
import org.smartbit4all.domain.service.query.QueryExecutionPlan;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.smartbit4all.domain.service.query.QueryResult;
import org.smartbit4all.domain.service.query.Retrieval;
import org.smartbit4all.domain.service.query.RetrievalRequest;
import org.smartbit4all.domain.service.query.RetrievalRound;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.domain.utility.crud.CrudRead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * The {@link CrudApi} implementation. It analyzes the incoming query requests to produce a
 * {@link QueryExecutionPlan}. This plan can be executed later on by the
 * {@link #executeQueryPlan(QueryExecutionPlan)} function.
 *
 */
@Service
public class CrudApiImpl implements CrudApi {

  private Map<String, CrudExecutionApi> executionApisByName;
  private QueryExecutorConfig config;
  protected DataSetApi dataSetApi;
  private TableDataApi tableDataApi;
  private IdentifierService identifierService;
  private ObjectApi objectApi;

  public CrudApiImpl(ApplicationContext appContext,
      @Autowired(required = false) QueryExecutorConfig config,
      @Autowired(required = false) DataSetApi dataSetApi,
      @Autowired(required = false) TableDataApi tableDataApi,
      @Autowired(required = false) IdentifierService identifierService,
      @Autowired(required = true) ObjectApi objectApi) {
    this.executionApisByName = appContext.getBeansOfType(CrudExecutionApi.class);
    this.config = config;
    this.dataSetApi = dataSetApi;
    this.tableDataApi = tableDataApi;
    this.identifierService = identifierService;
    this.objectApi = objectApi;
  }

  @Override
  public QueryExecutionPlan prepareQueries(QueryInput... queries) {
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
                if (expression.getStoredTableDataUri() != null) {
                  preQueryRead.fromTableData(expression.getStoredTableDataUri());
                }
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
                    new QueryAndSaveResultAsDataSet(dataSetApi, where, expression,
                        query.getTableDataUri() != null);

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
  public QueryResult executeQueryPlan(QueryExecutionPlan execPlan) {
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
  public Retrieval prepareRetrieval(RetrievalRequest request) {
    Retrieval retrieval = new Retrieval(request);
    return retrieval;
  }

  @Override
  public QueryOutput executeQuery(QueryInput queryInput) {
    QueryExecutionPlan executionPlan = prepareQueries(queryInput);
    QueryResult queryResult = executeQueryPlan(executionPlan);
    List<QueryOutput> results = queryResult.getResults();
    if (results == null || results.isEmpty()) {
      throw new IllegalStateException(
          "The query execution for the given QueryInput finished with no QueryResult!");
    }

    QueryOutput output = results.stream()
        .filter(r -> queryInput.getName().equals(r.getName()))
        .findAny()
        .orElse(null);

    if (output == null) {
      throw new IllegalStateException(
          "There is no matching QueryOutput for the input with name: " + queryInput.getName());
    }
    if (!output.hasResult()) {
      throw new IllegalStateException(
          "The query execution for the given QueryInput finished with no result!");
    }

    return output;
  }

  @Override
  public <E extends EntityDefinition> CreateOutput executeCreate(CreateInput<E> input) {
    return getExecutionApiForEntityDef(input.getEntityDefinition()).executeCreate(input);
  }

  @Override
  public <E extends EntityDefinition> UpdateOutput executeUpdate(UpdateInput<E> input) {
    return getExecutionApiForEntityDef(input.getEntityDefinition()).executeUpdate(input);
  }

  @Override
  public <E extends EntityDefinition> DeleteOutput executeDelete(DeleteInput<E> input) {
    return getExecutionApiForEntityDef(input.getEntityDefinition()).executeDelete(input);
  }

  /**
   * This is the inner execution point where the {@link CrudApi} decides which
   * {@link CrudExecutionApi} to use for the execution.
   *
   * @param queryInput The query input to execute. If it defines a TableData by URI then it will be
   *        mapped to the named {@link CrudExecutionApi}.
   * @return
   * @throws Exception
   */
  private QueryOutput executeWithoutPrepare(QueryInput queryInput) throws Exception {
    if (queryInput.getTableDataUri() != null && tableDataApi != null) {
      return executeQueryOnTableData(queryInput);
    }
    return getExecutionApiForEntityDef(queryInput.getEntityDef()).executeQuery(queryInput);
  }

  private final QueryOutput executeQueryOnTableData(QueryInput queryInput) throws Exception {
    TableData<?> tableData = tableDataApi.read(queryInput.getTableDataUri());

    ExpressionEvaluationPlan evaluationPlan =
        ExpressionEvaluationPlan.of(tableData, null, queryInput.where());

    List<DataRow> result = evaluationPlan.execute();

    QueryOutput out = new QueryOutput(queryInput.getName(), queryInput.entityDef());
    out.setTableData(TableDatas.copyRows(tableData, result, queryInput.properties()));

    if (queryInput.orderBys() != null && !queryInput.orderBys().isEmpty()) {
      tableDataApi.sort(out.getTableData(), queryInput.orderBys());
    }
    return out;

  }

  private CrudExecutionApi getExecutionApiForEntityDef(EntityDefinition entityDef) {
    if (config == null) {
      if (executionApisByName.size() != 1) {
        throw new IllegalStateException(
            "No QueryExecutorConfig configured. In this case, there must be only one QueryExecutionApi registered, but there is: "
                + executionApisByName.size());
      }
      return executionApisByName.values().iterator().next();
    }

    CrudExecutionApi execApi = getExecutionApi(entityDef);

    if (execApi == null) {
      throw new IllegalStateException(
          "There is no QueryExecutionApi configured for EntityDefinition: "
              + entityDef.entityDefName());
    }

    return execApi;
  }

  private CrudExecutionApi getExecutionApi(EntityDefinition entityDef) {
    return fromConfigExecApisByDomain(entityDef)
        .orElseGet(() -> fromConfigExecApiNamesByDomain(entityDef)
            .orElseGet(() -> fromConfigExecApisByEntityUri(entityDef)
                .orElseGet(() -> fromConfigExecApiNamesByEntityUri(entityDef)
                    .orElseGet(() -> fromDefaultConfig(entityDef)
                        .orElse(null)))));
  }

  private Optional<CrudExecutionApi> fromDefaultConfig(EntityDefinition entityDef) {
    CrudExecutionApi api = null;
    if (config.defaultExecutorApi != null) {
      api = executionApisByName.get(config.defaultExecutorApi);
    }
    return Optional.of(api);
  }

  @Override
  public boolean isExecutionApiExists(EntityDefinition entityDef) {
    if (config == null) {
      return false;
    }
    return getExecutionApi(entityDef) != null;
  }

  private Optional<CrudExecutionApi> fromConfigExecApiNamesByDomain(EntityDefinition entityDef) {
    return config.execApiNamesByDomain.entrySet().stream()
        .filter(es -> es.getKey().equals(entityDef.getDomain()))
        .findAny()
        .map(es -> executionApisByName.get(es.getValue()));
  }

  private Optional<CrudExecutionApi> fromConfigExecApiNamesByEntityUri(
      EntityDefinition entityDef) {
    return config.execApiNamesByEntityUri.entrySet().stream()
        .filter(es -> es.getKey().equals(entityDef.getUri()))
        .findAny()
        .map(es -> executionApisByName.get(es.getValue()));
  }

  private Optional<CrudExecutionApi> fromConfigExecApisByEntityUri(EntityDefinition entityDef) {
    return config.execApisByEntityUri.entrySet().stream()
        .filter(es -> es.getKey().equals(entityDef.getUri()))
        .findAny()
        .map(es -> es.getValue());
  }

  private Optional<CrudExecutionApi> fromConfigExecApisByDomain(EntityDefinition entityDef) {
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
    Map<URI, CrudExecutionApi> execApisByEntityUri;
    Map<String, CrudExecutionApi> execApisByDomain;
    Map<URI, String> execApiNamesByEntityUri;
    Map<String, String> execApiNamesByDomain;
    String defaultExecutorApi;


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
        CrudExecutionApi executionApi) {
      execApisByEntityUri.put(entityUri, executionApi);
      return this;
    }

    public QueryExecutorConfig addExecutionApiForDomain(String domainName,
        CrudExecutionApi executionApi) {
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

    public QueryExecutorConfig defaultExecutionApi(String executionApiName) {
      this.defaultExecutorApi = executionApiName;
      return this;
    }

  }

  @Override
  public Retrieval executeRetrieval(Retrieval retrieval) throws Exception {
    RetrievalRound round = retrieval.next();
    while (!round.getQueries().isEmpty()) {
      round.getQueries().entrySet().parallelStream().forEach(e -> {
        try {
          QueryOutput queryOutput = executeQuery(e.getValue());
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
    CrudExecutionApi executionApiForEntityDef = getExecutionApiForEntityDef(entityDef);
    return executionApiForEntityDef.getSchema();
  }

  @Override
  public <E extends EntityDefinition, T> TableData<E> lockOrCreateAndLock(E entityDef,
      Property<T> uniqueProperty, T uniqueValue, String sequenceName,
      Map<Property<?>, Object> extraValues) throws Exception {
    // We try to lock the records by querying with lock. We must collect the values of the unique
    // columns.
    TableData<E> result = Crud.read(entityDef)
        .selectAllProperties()
        .where(uniqueProperty.eq(uniqueValue))
        .lock()
        .listData();
    if (result.isEmpty()) {
      // The record is missing from the database so we try to insert it
      NextIdentifier next = identifierService.next();
      next.setInput(sequenceName);
      next.execute();
      TableData<E> td = TableDatas.of(entityDef, entityDef.allProperties());
      DataRow row = td.addRow();
      row.set(uniqueProperty, uniqueValue);
      // TODO We don't have the correct interface for the multiple primary key!
      for (Property<?> primaryProperty : entityDef.PRIMARYKEYDEF()) {
        row.setObject(primaryProperty, next.output());
      }
      if (extraValues != null) {
        for (Entry<Property<?>, Object> entry : extraValues.entrySet()) {
          row.setObject(entry.getKey(), entry.getValue());
        }
      }
      try {
        Crud.create(td);
        // In this case we managed to insert the record. Therefore it's already locked until the end
        // of the transaction.
        return td;
      } catch (Exception e) {
        // We failed to insert the record. Someone else could insert it. We can call ourselves
        // recursively to query.
        // TODO Check if it's the unique index violation exception from the database. We need to
        // catch the DuplicateKeyException but it's tx module!
        return lockOrCreateAndLock(entityDef, uniqueProperty, uniqueValue, sequenceName,
            extraValues);
      }
    }
    return result;
  }

  @Override
  public ObjectApi getObjectApi() {
    return objectApi;
  }
}
