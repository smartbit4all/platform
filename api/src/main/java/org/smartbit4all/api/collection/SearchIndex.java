package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
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
  SearchEntityDefinition getDefinition();

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
   * @param filterExpressions The expression list for the query.
   * @return The result table data of the search.
   */
  TableData<?> executeSearch(FilterExpressionList filterExpressions);

  /**
   * We can execute the search synchronously and we get back the result {@link TableData} in memory.
   * 
   * @param queryInput The query input that defines the logical filter conditions.
   * @return The table data in memory.
   */
  List<O> list(QueryInput queryInput);

  /**
   * Read all the objects specified by the uri stream and load the table data with the values.
   * 
   * @param uris The uri stream to load.
   * @return The result table data.
   */
  TableData<?> tableDataOfUris(Stream<URI> uris);

  /**
   * Read all the objects specified by the object stream and load the table data with the values.
   * 
   * @param objects The object stream to start with.
   * @return The result table data.
   */
  TableData<?> tableDataOfObjects(Stream<O> objects);

  /**
   * Returns all the available filter fields for the given search index.
   * 
   * @return
   */
  FilterExpressionFieldList allFilterFields();

}
