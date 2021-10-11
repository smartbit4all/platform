package org.smartbit4all.domain.service.modify;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.core.SB4CompositeFunctionImpl;
import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.utility.crud.Crud;

/**
 * An operation could be {@link Create}, {@link Update} or {@link Delete}. One operation is
 * 
 * @author Peter Boros
 */
public class ApplyChangeOperation extends SB4CompositeFunctionImpl<Void, List<DataRow>> {

  /**
   * The {@link TableData} that contains the data for this operation. This table data will be
   * referred by the consumers of this operation. They will access this table data where
   */
  private final TableData<?> tableData;

  /**
   * The one and only row in the operation {@link #tableData}.
   */
  private DataRow row;

  /**
   * The unique identifier that must be set to be able to identify the given record. Generally a
   * UUID can be used to identify the objects before storing it to the database. In this way the
   * referential integrity of the database is based on this unique UUIDs.
   */
  private Property<?> uniqueId;

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
   * The mappings for copying the result of this operation to other operations input.
   */
  private final List<ApplyChangeOperationTargetMapping> mappings = new ArrayList<>();

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
            Crud.update(tableData);
          }
        } else if (operation == ChangeOperation.MODIFY) {
          Crud.update(tableData);
        } else {
          Crud.delete(tableData);
        }
      }

    });
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

  /**
   * The mappings
   * 
   * @return
   */
  public final List<ApplyChangeOperationTargetMapping> getMappings() {
    return mappings;
  }

  public final Property<?> getUniqueId() {
    return uniqueId;
  }

  public final void setUniqueId(Property<?> uniqueId) {
    this.uniqueId = uniqueId;
  }

}
