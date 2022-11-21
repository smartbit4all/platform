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

  private List<QueryOutput> resultQueries = new ArrayList<>();

  public List<QueryOutput> getResultsInner() {
    return resultQueries;
  }

  public List<QueryOutput> getResults() {
    return Collections.unmodifiableList(resultQueries);
  }

}
