package org.smartbit4all.domain.data;

import java.util.List;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.Reference;

/**
 * This column is a referred column from another {@link TableData}. It's based on {@link Reference}
 * meta and references rows in the same order we have in our own data table.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public class DataColumnRef<T> extends DataColumn<T> {

  /**
   * This is the join path to access the property at the end.
   */
  final List<DataReference<?, ?>> referencePath;

  DataColumnRef(List<DataReference<?, ?>> referencePath, PropertyRef<T> property) {
    super(property);
    this.referencePath = referencePath;
  }

  @Override
  public T getValue(DataRow row) {
    DataRow finalRow = getFinalRow(row);
    if (finalRow != null) {
      return getOwnedColumn().getValue(finalRow);
    }
    return null;
  }

  @Override
  void setValue(DataRow row, Object value) {
    DataRow finalRow = getFinalRow(row);
    if (finalRow != null) {
      getOwnedColumn().setValue(finalRow, value);
    }
  }

  /**
   * This is a traverse for the reference path. We walk until we reach the end of the path or we get
   * a missing reference.
   * 
   * @param row The initial row.
   * @return The row of the final {@link TableData} or null if doesn't have a continuous path till
   *         the referred column.
   */
  private final DataRow getFinalRow(DataRow row) {
    DataRow currentRow = row;
    for (DataReference<?, ?> dataReference : referencePath) {
      DataRow refDataRow = dataReference.getReferredTargetRows().get(currentRow.getRowDataIndex());
      if (refDataRow == null) {
        return null;
      }
      currentRow = refDataRow;
    }
    return currentRow;
  }

  /**
   * Return the reference path of the give column.
   * 
   * @return
   */
  public final List<DataReference<?, ?>> getReferencePath() {
    return referencePath;
  }

}
