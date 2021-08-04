package org.smartbit4all.domain.service.query;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.core.SB4Function;
import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.SortOrderProperty;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * The utility functions for the {@link QueryApi} and its implementations.
 */
@Service
public class Queries implements InitializingBean {

  private static Queries instance;

  private QueryApi queryApi;

  /**
   * To avoid on demand instantiation.
   */
  public Queries(QueryApi queryApi) {
    this.queryApi = queryApi;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    instance = this;
  }

  public static final QueryApi getQueryApi() {
    if (instance == null) {
      throw new IllegalStateException(
          "There is no QueryApi implementation registered in the Spring Context!");
    }
    return instance.queryApi;
  }

  public static final QueryOutput execute(QueryInput queryInput) throws Exception {
    return getQueryApi().execute(queryInput);
  }

  public static final SB4Function<QueryInput, QueryOutput> asFunction(QueryInput queryInput) {
    return new QueryFunction(queryInput, Queries::execute);
  }

  public static final SB4Function<QueryInput, QueryOutput> asFunction(QueryInput queryInput,
      QueryExecution execution) {
    return new QueryFunction(queryInput, execution);
  }

  public static final URI constructQueryURI(String categoryPath) {
    try {
      return new URI("query", null, "/" + categoryPath, UUID.randomUUID().toString());
    } catch (URISyntaxException e) {
      throw new RuntimeException(
          "Unable to construct the URI for the " + categoryPath + " query path", e);
    }
  }

  public static final QueryInput copy(QueryInput inputToCopy) {
    QueryInput result = new QueryInput();
    result.name = inputToCopy.name;
    result.properties.addAll(inputToCopy.properties);
    result.where = inputToCopy.where != null ? inputToCopy.where.copy() : null;
    result.sortOrders
        .addAll(inputToCopy.sortOrders.stream().map(SortOrderProperty::copy)
            .collect(Collectors.toList()));
    result.groupByProperties.addAll(inputToCopy.groupByProperties);
    result.setLockRequest(inputToCopy.getLockRequest().copy());
    result.distinct = inputToCopy.distinct;
    result.entityDef = inputToCopy.entityDef;
    return result;
  }

  public static final QueryInput copyTranslated(QueryInput inputToCopy,
      EntityDefinition translatedEntity, List<Reference<?, ?>> joinPath) {
    // TODO
    return null;
  }

  public static interface QueryExecution {
    QueryOutput execute(QueryInput input) throws Exception;
  }

  public static class QueryFunction extends SB4FunctionImpl<QueryInput, QueryOutput> {

    private QueryExecution execution;

    private QueryFunction(QueryInput queryInput, QueryExecution execution) {
      this.input = queryInput;
      this.execution = execution;
      this.output = new QueryOutput();
      output.setName(input.getName());
    }

    @Override
    public void execute() throws Exception {
      QueryOutput result = execution.execute(input);
      if (output.getTableData() == null) {
        output.setTableData(result.getTableData());
      } else {
        /*
         * clear the table data no matter if it had previous data. The output must contain exact
         * result of the query
         */
        output.getTableData().clearRows();
        TableDatas.append(output.getTableData(), result.getTableData());
      }
    }

  }

}
