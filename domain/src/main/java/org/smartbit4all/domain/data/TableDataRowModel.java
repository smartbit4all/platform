package org.smartbit4all.domain.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This extension is responsible for managing the rows of a given {@link TableData}. The model
 * contains a list of {@link DataRow} that is the currently valid rows in the data table. If we add
 * only new rows then the list of rows are the same as we can see them on the interface of the data
 * table. But if we change the order or delete a row then the list will show the public rows and in
 * the background all the indices in the rows are maintained. This object will remember the wasted
 * rows and reuse them if another new row is added.
 * 
 * This abstraction is useful to avoid administration overhead of the row modification (ordering,
 * removing etc.). Later on the hidden rows could be reused again if we add another row. If we would
 * like to shrink the value list of the columns then we use this structure also.
 * 
 * @author Peter Boros
 *
 */
final class TableDataRowModel {

  private static final String IVALID_ROW = "Ivalid row";

  /**
   * TODO The reference of the {@link TableData} that is managed by this model. Reference to make it
   * easier to garbage.
   */
  final WeakReference<TableData<?>> tableDataRef;

  /**
   * The public rows in the current order.
   */
  final List<DataRow> rows = new ArrayList<>();

  /**
   * The hidden rows are added to this list and we can reuse them for new rows again. First we
   * remove the row from the {@link #rows} list and add to this one.
   */
  final List<DataRow> hiddenRows = new ArrayList<>();

  /**
   * Creates a new row model for the given data table. The data table is referred
   * 
   * @param tableData
   */
  TableDataRowModel(TableData<?> tableData) {
    super();
    this.tableDataRef = new WeakReference<>(tableData);
  }

  final TableData<?> tableData() {
    return tableDataRef.get();
  }

  /**
   * @return Return the {@link #rows} in an unmodifiable list.
   */
  final List<DataRow> rows() {
    return Collections.unmodifiableList(rows);
  }

  /**
   * Validate if the given row has a valid position and in this position we have the same row.
   * 
   * @param row The row to check.
   * @param notNull If true then the row must be set and exception will be thrown if null.
   * @throws InvalidRowException
   * @return The position of the given row.
   */
  private final int checkRow(DataRow row, boolean notNull) {
    // TODO java9 Objects.checkIndex
    if (row == null) {
      if (notNull) {
        throw new IllegalArgumentException(IVALID_ROW);
      }
      return DataRow.I_INVALIDINDEX;
    }
    if (row.getPosition() < 0 && row.getPosition() >= rows.size()) {
      throw new IllegalArgumentException(IVALID_ROW + row.toString());
    }
    DataRow currentRow = rows.get(row.getPosition());
    if (row != currentRow) {
      throw new IllegalArgumentException(IVALID_ROW + row.toString());
    }
    return row.getPosition();
  }

  /**
   * To hide the given row we access the row in the {@link #rows} list by the position of the
   * parameter. If they are equal then we can remove it from the {@link #rows} list and add to the
   * {@link #hiddenRows}. It doesn't modify the {@link DataColumn} values list.
   *
   * @param row The row to hide.
   */
  final void hideRow(DataRow row) {
    checkRow(row, true);

    // We append the row to the hidden rows list.
    hiddenRows.add(row);

    // We remove the given row from the row list and modify the position of the successor rows.
    rows.remove(row.getPosition());

    // Modify the position of the remaining element after the range.
    rows.subList(row.getPosition(), rows.size()).stream().forEach(r -> r.modifyPosition(-1));
    for (int i = row.getPosition(); i < rows.size(); i++) {
      rows.get(i).modifyPosition(-1);
    }

    // And finally invalidate the removed row.
    row.invalidate();

  }

  /**
   * To hide the given rows we access the row in the {@link #rows} list by the position of the
   * parameter. If they are equal then we can remove it from the {@link #rows} list and add to the
   * {@link #hiddenRows}. It doesn't modify the {@link DataColumn} values list. We must support this
   * instead of calling the {@link #hideRow(DataRow)} for every row to achieve better performance.
   * If any parameter is null then we don't modify the row model.
   * 
   * @param fromRow The start row of the row range to remove. Inclusive
   * @param toRow The end of the row range. Inclusive.
   * @throws InvalidRowException
   */
  final void hideRange(DataRow fromRow, DataRow toRow) {
    if (fromRow == null || toRow == null) {
      return;
    }

    int fromIndex = checkRow(fromRow, true);
    int toIndex = checkRow(toRow, true);
    if (fromIndex > toIndex) {
      // There is no effect.
      return;
    }
    // The number of rows to hide.
    int numberOfRows = toIndex - fromIndex + 1;

    // We append the rows to the hidden rows list.
    List<DataRow> range = rows.subList(fromIndex, toIndex);
    hiddenRows.addAll(range);

    range.stream().forEach(r -> r.invalidate());

    // We remove the given row from the row list and modify the position of the successor rows.
    range.clear();

    // Modify the position of the remaining element after the range.
    rows.subList(fromIndex, rows.size()).stream().forEach(r -> r.modifyPosition(-numberOfRows));

  }

  /**
   * Hide a list of rows. They won't appear in any other operation but can be reused. It will be
   * handled by the {@link #hiddenRows} list.
   *
   * @param dataRows The list of data rows to hide. .
   */
  final void hideRows(List<DataRow> dataRows) {
    if (dataRows == null || dataRows.isEmpty()) {
      return;
    }

    for (DataRow row : dataRows) {
      checkRow(row, true);
    }

    // We append the row to the hidden rows list.
    hiddenRows.addAll(dataRows);


    /*
     * We remove the given row from the row list. To avoid inconsistent modification we use the
     * descending order.
     */
    Collections.sort(dataRows, DataRow.ComparatorByPositionDesc);
    dataRows.stream().forEach(r -> rows.remove(r.getPosition()));

    // Modify the position of the elements after the first row.
    int firstPosition = dataRows.get(dataRows.size() - 1).getPosition();
    repairPostions(firstPosition);

    // And finally invalidate the removed row.
    dataRows.stream().forEach(r -> r.invalidate());
  }

  /**
   * Repair the position index of the rows.
   * 
   * @param firstPosition The first index where the position must be repaired.
   */
  private final void repairPostions(int firstPosition) {
    for (int i = firstPosition; i < rows.size(); i++) {
      rows.get(i).setPosition(i);
    }
  }


  /**
   * The <code>addRow</code> add the given number of rows to the model.
   *
   * @param newRows The number of rows to add.
   * @param row The position of the new rows could be defined by this row. If it's null then all the
   *        new rows will be placed at the end of the list.
   * @param after If it's true then the new rows will be added after the row parameter. If false the
   *        they will appear before this row.
   * @throws InvalidRowException
   */
  final List<DataRow> addRow(int newRows, DataRow row, boolean after) {

    // If there is no new row then returns an empty list.
    if (newRows <= 0) {
      return Collections.emptyList();
    }

    // If we have some problem with row then exception will be thrown.
    checkRow(row, false);

    List<DataRow> result = new ArrayList<>(newRows);

    /*
     * Here we reuse as many deleted rows as we can. These are new row so the content must be
     * cleared. We remember the first new row to be able to set the position at the end.
     */
    int firstNewRow = row == null ? rows.size() : row.getPosition();
    int numberOfReusedRows = Math.min(hiddenRows.size(), newRows);
    if (numberOfReusedRows > 0) {
      List<DataRow> reusedRows =
          hiddenRows.subList(hiddenRows.size() - numberOfReusedRows, hiddenRows.size() - 1);
      /*
       * Restore the hidden rows to the proper position. The problem is that adding many element to
       * the lists are not so effective!
       */
      if (row == null) {
        rows.addAll(reusedRows);
      } else if (after) {
        hiddenRows.stream().forEach(r -> rows.add(row.getPosition() + 1, r));
      } else {
        hiddenRows.stream().forEach(r -> rows.add(row.getPosition(), r));
      }
      // clear the data from the reused rows.
      tableData().columns().parallelStream().filter(col -> col instanceof DataColumnOwned<?>)
          .forEach(c -> reusedRows.parallelStream().forEach(r -> {
            ((DataColumnOwned<?>) c).values().set(r.getRowDataIndex(), null);
          }));
      tableData().referenceTo().parallelStream()
          .forEach(ref -> reusedRows.parallelStream().forEach(r -> {
            ref.getReferredTargetRows().set(r.getRowDataIndex(), null);
            ref.getDetailSourceRows().set(r.getRowDataIndex(), null);
          }));
      result.addAll(reusedRows);
    }

    /*
     * Here we add the rest of the rows at the end of the row list.
     */
    int brandNewRows = newRows - numberOfReusedRows;
    if (brandNewRows > 0) {
      /*
       * We create the data rows first. The position and the data index will be the natural index
       * from the row list. After that we need to add the values to the columns and references.
       */
      // TODO Continue to add at the proper position.
      for (int i = 0; i < brandNewRows; i++) {
        DataRow dataRow = new DataRow(tableData(), rows.size(), rows.size());
        if (row == null) {
          rows.add(dataRow);
        } else if (after) {
          rows.add(row.getPosition() + 1, dataRow);
        } else {
          rows.add(row.getPosition(), dataRow);
        }
        result.add(dataRow);
      }

      tableData().columns().parallelStream().filter(col -> col instanceof DataColumnOwned<?>)
          .forEach(c -> {
            // TODO It must be much more effective! Use some other list may be.
            ((DataColumnOwned<?>) c).ensureCapacity(rows.size());
            for (int i = 0; i < brandNewRows; i++) {
              ((DataColumnOwned<?>) c).values().add(null);
            }
          });
      tableData().referenceTo().parallelStream().forEach(ref -> {
        // TODO It must be much more effective! Use some other list may be.
        ref.ensureCapacityReferredTargetRows(rows.size());
        for (int i = 0; i < brandNewRows; i++) {
          ref.getReferredTargetRows().add(null);
        }
        // TODO It must be much more effective! Use some other list may be.
        ref.ensureCapacityDetailSourceRows(rows.size());
        for (int i = 0; i < brandNewRows; i++) {
          ref.getDetailSourceRows().add(null);
        }
      });
    }
    // Repair the positions of the new rows let them be reused or brand new ones.
    repairPostions(firstNewRow);
    return result;
  }

  /**
   * Clear model --> clear all rows.
   */
  final void clear() {
    rows.clear();
    hiddenRows.clear();
  }
}
