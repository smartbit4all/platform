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
    primaryIndex = tableDataStorage.index().unique(primaryKey);
    this.primaryKey = primaryKey;
  }

  @Override
  protected void fillRow(TableData<?> tableData, DataRow rowToFill) {
    DataColumn<?> primaryCol = tableData.getColumn(primaryKey);
    for (DataColumn<?> column : tableData.columns()) {
      DataColumn<?> storageColumn = tableDataStorage.getColumn(column.getProperty());
      Object primaryValue = tableData.get(primaryCol, rowToFill);
      tableData.setObject(column, rowToFill,
          tableDataStorage.get(storageColumn, primaryIndex.getObject(primaryValue)));
    }
  }

  @Override
  public void loadAllRows(TableData<?> tableData) {
    // TableDatas.copy(tableDataStorage, tableData);
  }

}
