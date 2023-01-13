package org.smartbit4all.api.collection;

import java.util.List;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.query.QueryInput;

/**
 * This is the abstract interface of the search index based on {@link TableData} and
 * {@link EntityDefinition}. We always have an available search definition and we can run
 * {@link QueryInput} against the index. The result is a {@link TableData} that contains the
 * required values.
 * 
 * @author Peter Boros
 */
public interface SearchIndex<O> {

  /**
   * @return The schema of the search index as a namespace for the search index.
   */
  String logicalSchema();

  /**
   * @return The unique name of the search index.
   */
  String name();

  /**
   * The definition of the search index.
   * 
   * @return
   */
  EntityDefinition getDefinition();

  /**
   * We can execute the search synchronously and we get back the result {@link TableData} in memory.
   * 
   * @param queryInput The query input that defines the logical filter conditions.
   * @return The table data in memory.
   */
  TableData<?> executeSearch(QueryInput queryInput);

  /**
   * We can execute the search synchronously and we get back the result {@link TableData} in memory.
   * 
   * @param queryInput The query input that defines the logical filter conditions.
   * @return The table data in memory.
   */
  List<O> list(QueryInput queryInput);

}
