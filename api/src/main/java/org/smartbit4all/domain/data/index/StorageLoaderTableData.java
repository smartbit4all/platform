package org.smartbit4all.domain.data.index;

import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

public class StorageLoaderTableData<E extends EntityDefinition> extends StorageLoader {

  private final TableData<E> tableDataStorage;

  private UniqueIndex<?> primaryIndex;

  Property<?> primaryKey;

  public StorageLoaderTableData(E entityDef, TableData<E> td, Property<?> primaryKey) {
    super(entityDef);

    this.tableDataStorage = td;
    this.primaryIndex = tableDataStorage.index().unique(primaryKey);
    this.primaryKey = primaryKey;
  }

  @Override
  protected void fillRow(TableData<?> tableData, DataRow rowToFill) {
    Object primaryValue = rowToFill.get(primaryKey);
    DataRow storageRow = primaryIndex.getObject(primaryValue);

    DataRow row = getRow(tableData, rowToFill);
    for (DataColumn<?> column : tableDataStorage.columns()) {
      Property<?> property = column.getProperty();
      DataColumn<?> columnInTable = tableData.getColumn(property);
      if (columnInTable != null) {
        Object indexValue = storageRow.get(property);
        tableData.setObject(columnInTable, row, indexValue);
      }
    }
  }

  private DataRow getRow(TableData<?> tableData, DataRow dataRow) {
    for (DataRow actualRow : tableData.rows()) {
      if (dataRow == actualRow) {
        return actualRow;
      }
    }
    return tableData.addRow();
  }

  @Override
  public void loadAllRows(TableData<?> tableData) {
    for (DataRow row : tableDataStorage.rows()) {
      DataRow newRow = tableData.addRow();

      for (Property<?> property : tableDataStorage.properties()) {
        newRow.setObject(property, row.get(property));
      }
    }
  }

}
