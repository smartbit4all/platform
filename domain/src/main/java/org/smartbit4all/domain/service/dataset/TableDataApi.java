package org.smartbit4all.domain.service.dataset;

import java.net.URI;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.service.CrudApi;

/**
 * The {@link TableDataApi} is responsible for storing and retrieving the {@link TableData}s as a
 * set of record. The {@link CrudApi} use this to manage the temporary record sets during the query
 * process and there can be record sets with longer storage period.
 * 
 * @author Peter Boros
 */
public interface TableDataApi {

  /**
   * This saves a table data.
   * 
   * @param tableData The table data itself.
   * @return The URI for reading.
   */
  URI save(TableData<?> tableData);

  /**
   * Read the {@link TableData} identified by the URI.
   * 
   * @param uri The unique identifier.
   * @return
   */
  TableData<?> read(URI uri);

  void delete(URI uri);

}
