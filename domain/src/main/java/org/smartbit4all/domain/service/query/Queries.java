package org.smartbit4all.domain.service.query;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.core.SB4Function;
import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.SortOrderProperty;

public class Queries {

  private Queries() {
    super();
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
    result.setLockRequest(
        inputToCopy.getLockRequest() == null ? null : inputToCopy.getLockRequest().copy());
    result.distinct = inputToCopy.distinct;
    result.entityDef = inputToCopy.entityDef;
    return result;
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
      this.output = new QueryOutput(input.getName(), queryInput.entityDef);
    }

    @Override
    public void execute() throws Exception {
      QueryOutput result = execution.execute(input);
      output.copyResult(result);
    }

  }

}
