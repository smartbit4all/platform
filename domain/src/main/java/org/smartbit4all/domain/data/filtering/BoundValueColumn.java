package org.smartbit4all.domain.data.filtering;

import java.lang.ref.WeakReference;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.OperandBoundValue;

/**
 * The bound value based on the {@link TableData}. It has a column and a row to identify the value.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
class BoundValueColumn<T> extends OperandBoundValue<T> {

  /**
   * The data table that contains the data.
   */
  private WeakReference<TableData<?>> tableDataRef;

  /**
   * The column related to the property in the given {@link #tableDataRef}
   */
  private DataColumn<T> column;

  /**
   * The row for the record.
   */
  private DataRow row;

  BoundValueColumn(TableData<?> tableData, DataColumn<T> column) {
    super();
    this.tableDataRef = new WeakReference<TableData<?>>(tableData);
    this.column = column;
  }

  final void setRow(DataRow row) {
    this.row = row;
  }

  @Override
  public T getValue() {
    if (tableDataRef == null) {
      return null;
    }
    TableData<?> tableData = tableDataRef.get();
    if (tableData == null) {
      return null;
    }
    return tableData.get(column, row);
  }

}
