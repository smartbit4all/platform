package org.smartbit4all.api.grid;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.domain.data.TableData;

public interface GridApi {

  /**
   * If we have a well defined list bean and we would like to save it.
   * 
   * @param <T>
   * @param clazz
   * @param o
   * @param columns
   * @return
   */
  <T> GridModel modelOf(Class<T> clazz, List<T> o, Map<String, String> columns);

  /**
   * If we already have a table data as the result of a query and we would like to save it as
   * content of a grid.
   * 
   * @param tableData The table data.
   * @return
   */
  GridModel modelOf(TableData<?> tableData);

  /**
   * If we have a well defined list bean and we would like to save it's grid model by the definition
   * of a {@link SearchIndex}.
   * 
   * @param searchIndex The search index to use to construct the table data result.
   * @param uris The list of URI.
   * @return
   */
  GridModel modelOfUris(SearchIndex<?> searchIndex, Stream<URI> uris);

  /**
   * If we have a well defined list bean and we would like to save it's grid model by the definition
   * of a {@link SearchIndex}.
   * 
   * @param <T>
   * @param searchIndex The search index to use to construct the table data result.
   * @param objects The list of objects.
   * @return
   */
  <T> GridModel modelOfObjects(SearchIndex<T> searchIndex, Stream<T> objects);

}
