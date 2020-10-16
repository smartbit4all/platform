package org.smartbit4all.domain.data.index;

import java.util.Map;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.utility.CompositeValue;

/**
 * The abstract superclass of the composite indices.
 * 
 * @author Peter Boros
 */
public abstract class CompositeUniqueIndex extends TableDataIndex {

  /**
   * If the given column requires a fast access to the row and the column value is unique then this
   * map will be maintained to ensure. If the given column is indexed in this way then we can use
   * this index for filtering and accessing the relevant rows by the value of this column.
   */
  protected Map<CompositeValue, DataRow> index = null;

  CompositeUniqueIndex(TableData<?> tableData) {
    super(tableData, IndexType.UNIQUE, true);
  }

}
