package org.smartbit4all.domain.service.modify;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.core.SB4CompositeFunctionImpl;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;

/**
 * An operation could be {@link Create}, {@link Update} or {@link Delete}. One opartion
 * 
 * @author Peter Boros
 */
public class ApplyChangeOperation extends SB4CompositeFunctionImpl<Void, List<DataRow>> {

  /**
   * The {@link TableData} that contains the data for this operation. This table data will be
   * referred by the consumers of this operation. They will access this table data where
   */
  private TableData<?> tableData;

  /**
   * The one and only row in the operation {@link #tableData}.
   */
  private DataRow row;

  /**
   * The operations for the change..
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

  private final List<ApplyChangeOperationTargetMapping> mappings = new ArrayList<>();

  public ApplyChangeOperation(TableData<?> tableData, ChangeOperation operation) {
    super();
    this.tableData = tableData;
    if (tableData.isEmpty()) {
      row = tableData.addRow();
    } else {
      row = tableData.rows().get(0);
    }
    this.operation = operation;
  }

  public final TableData<?> getTableData() {
    return tableData;
  }

  public final DataRow getRow() {
    return row;
  }

  public final ChangeOperation getOperation() {
    return operation;
  }

  public final void setOperation(ChangeOperation operation) {
    this.operation = operation;
  }

  public final List<ApplyChangeOperationTargetMapping> getMappings() {
    return mappings;
  }

}
