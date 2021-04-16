package org.smartbit4all.domain.service.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The result of the query execution. It will have all the result values that are required by the
 * caller.
 * 
 * @author Peter Boros
 */
public final class QueryResult {

  private List<Query<?>> resultQueries = new ArrayList<>();

  public List<Query<?>> getResultsInner() {
    return resultQueries;
  }

  public List<Query<?>> getResults() {
    return Collections.unmodifiableList(resultQueries);
  }

}
