package org.smartbit4all.domain.data;

import java.util.List;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;

/**
 * Define a unit of logic that defines the subset of a {@link TableData}. It provides an
 * {@link Expression} to evaluate if a given row belongs to the set or not. On the other hand it
 * provides a preparation mechanism to setup a row to be included in the set.
 * 
 * @author Peter Boros
 */
public abstract class RecordSetDefinition<E extends EntityDefinition> {

  /**
   * Helps to filter which rows belongs to the given {@link TableDataRecordSet}. It's instantiated
   * for every {@link TableDataRecordSet}.
   * 
   * @param tableData The data table to filter.
   * @param rows The rows to evaluate.
   * @return The relevant rows that match the set condition.
   */
  abstract List<DataRow> filter(TableData<E> tableData, List<DataRow> rows);

  /**
   * The reverse operation. If a new row is added through the {@link TableDataRecordSet} then with
   * this function the definition might ensure that this the new row will belong to the given record
   * set.
   * 
   * @param tableData The data table.
   * @param rows The rows to prepare.
   */
  abstract void set(TableData<E> tableData, List<DataRow> rows);

}
