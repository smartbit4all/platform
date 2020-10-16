package org.smartbit4all.domain.service.modify;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.PropertyOwned;

public abstract class GenericModifyInput<E extends EntityDefinition> {

  TableData<E> tableData;

  List<DataColumn<?>> columns = null;

  List<PropertyOwned<?>> properties = null;

  List<DataColumn<?>> idColumns = null;

  List<PropertyOwned<?>> idProperties = null;

  ListIterator<DataRow> iterator;

  DataRow row;

  public GenericModifyInput(TableData<E> tableData) {
    super();
    this.tableData = tableData;
  }

  public List<PropertyOwned<?>> properties() {
    if (properties == null) {
      List<DataColumn<?>> mycolumns = new ArrayList<>(tableData.columns().size());
      List<PropertyOwned<?>> myproperties = new ArrayList<>(tableData.columns().size());
      for (DataColumn<?> column : tableData.columns()) {
        if (column.getProperty() instanceof PropertyOwned<?>
            && !tableData.entity().PRIMARYKEYDEF().contains(column.getProperty())) {
          myproperties.add((PropertyOwned<?>) column.getProperty());
          mycolumns.add(column);
        }
      }
      properties = myproperties;
      columns = mycolumns;
    }
    return properties;
  }

  public List<PropertyOwned<?>> identifiedBy() {
    if (idProperties == null) {
      List<DataColumn<?>> mycolumns = new ArrayList<>(2);
      List<PropertyOwned<?>> myproperties = new ArrayList<>(2);
      for (DataColumn<?> column : tableData.columns()) {
        if (column.getProperty() instanceof PropertyOwned<?>
            && tableData.entity().PRIMARYKEYDEF().contains(column.getProperty())) {
          myproperties.add((PropertyOwned<?>) column.getProperty());
          mycolumns.add(column);
        }
      }
      idProperties = myproperties;
      idColumns = mycolumns;
    }
    return idProperties;
  }

  public void start() {
    iterator = tableData.rows().listIterator();
  }

  public boolean next() {
    if (iterator == null) {
      start();
    }
    if (iterator.hasNext()) {
      row = iterator.next();
      return true;
    }
    return false;
  }

  public Object get(int index) {
    return tableData.get(columns.get(index), row);
  }

  public Object getIdentifier(int index) {
    return tableData.get(idColumns.get(index), row);
  }

}
