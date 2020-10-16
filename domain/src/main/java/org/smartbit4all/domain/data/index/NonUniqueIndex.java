package org.smartbit4all.domain.data.index;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;

/**
 * @author Peter Boros
 *
 */
public class NonUniqueIndex<T> extends TableDataIndex {

  /**
   * If the given column requires a fast access to the row and the column value is unique then this
   * map will be maintained to ensure. If the given column is indexed in this way then we can use
   * this index for filtering and accessing the relevant rows by the value of this column.
   */
  private Map<T, List<DataRow>> index = null;

  /**
   * The reference for the table data column.
   */
  private WeakReference<DataColumn<T>> columnRef;

  NonUniqueIndex(TableData<?> tableData, Property<T> property) {
    super(tableData, IndexType.NONUNIQUE, false);
    DataColumn<T> column = tableData.getColumn(property);
    columnRef = new WeakReference<DataColumn<T>>(column);
    init(tableData, column);
  }

  NonUniqueIndex(TableData<?> tableData, DataColumn<T> column) {
    super(tableData, IndexType.NONUNIQUE, false);
    columnRef = new WeakReference<DataColumn<T>>(column);
    init(tableData, column);
  }

  private void init(TableData<?> tableData, DataColumn<T> column) {
    index =
        tableData.rows().stream().collect(Collectors.groupingBy(r -> tableData.get(column, r)));
  }

  public List<DataRow> get(T value) {
    return index.get(value);
  }

}
