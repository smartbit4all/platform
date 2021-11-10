package org.smartbit4all.domain.service.modify.applychange;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.SB4CompositeFunctionImpl;
import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.service.modify.Create;
import org.smartbit4all.domain.service.modify.Delete;
import org.smartbit4all.domain.service.modify.Update;
import org.smartbit4all.domain.service.modify.UpdateOutput;
import org.smartbit4all.domain.utility.crud.Crud;

/**
 * An operation could be {@link Create}, {@link Update} or {@link Delete}. One operation is
 * 
 * @author Peter Boros
 */
public class ApplyChangeOperation extends SB4CompositeFunctionImpl<Void, List<DataRow>> {

  private static final Logger log = LoggerFactory.getLogger(ApplyChangeOperation.class);

  /**
   * The {@link TableData} that contains the data for this operation. This table data will be
   * referred by the consumers of this operation. They will access this table data where
   */
  private final TableData<?> tableData;

  /**
   * The one and only row in the operation {@link #tableData}.
   */
  private DataRow row;

  private String uniqueId;

  /**
   * The operations for the change.
   * 
   * @author Peter Boros
   */
  public static enum ChangeOperation {
    CREATE, MODIFY, DELETE
  }

  /**
   * The preferred operation for the change.
   */
  private ChangeOperation operation = ChangeOperation.CREATE;

  /**
   * Constructs a new apply change operation.
   * 
   * @param tableData The table data for the change operation.
   * @param operation The preferred operation for the change. In case of delete operation the
   *        mapping means setting nulls to the target!
   */
  public ApplyChangeOperation(TableData<?> tableData, ChangeOperation operation) {
    super();
    this.tableData = tableData;
    if (tableData.isEmpty()) {
      row = tableData.addRow();
    } else {
      row = tableData.rows().get(0);
    }
    this.operation = operation;
    call(new SB4FunctionImpl<Void, Void>() {

      @Override
      public void execute() throws Exception {
        if (operation == ChangeOperation.CREATE) {
          try {
            Crud.create(tableData);
          } catch (Exception e) {
            // Not too fail safe but we believe if we have have exception during the insert then we
            // try to update instead.
            log.info("Crud.create had exception during applyChange! Fallback to Crud.update...",
                e);
            Crud.update(tableData);
          }
        } else if (operation == ChangeOperation.MODIFY) {
          if (!isTableDataValid(tableData)) {
            // FIXME it wont be
            return;
          }
          UpdateOutput updateOutput = Crud.update(tableData);
          if (updateOutput.getUpdateCount() != tableData.size()) {
            // Try to insert instead of update
            Crud.create(tableData);
          }
        } else {
          Crud.delete(tableData);
        }
      }


    });
  }

  /**
   * The {@link TableData} is valid only if there is at least one column that is not a primary key.
   */
  private boolean isTableDataValid(TableData<?> tableData) {
    PropertySet primarykeydef = tableData.entity().PRIMARYKEYDEF();
    for (DataColumn<?> dataColumn : tableData.columns()) {
      if (!primarykeydef.contains(dataColumn.getProperty())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return The {@link TableData} of the change operation.
   */
  public final TableData<?> getTableData() {
    return tableData;
  }

  /**
   * @return The row of the operation.
   */
  public final DataRow getRow() {
    return row;
  }

  /**
   * @return The preferred operation for the change.
   */
  public final ChangeOperation getOperation() {
    return operation;
  }

  /**
   * The preferred operation for the change.
   * 
   * @param operation
   */
  public final void setOperation(ChangeOperation operation) {
    this.operation = operation;
  }

  public final String getUniqueId() {
    return uniqueId;
  }

  public final void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

}
