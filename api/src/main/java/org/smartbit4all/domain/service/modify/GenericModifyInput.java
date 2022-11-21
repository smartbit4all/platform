/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.service.modify;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.PropertyOwned;

/**
 * The generic base structure of all the modification input structure.
 * 
 * @author Peter Boros
 *
 * @param <E>
 */
public abstract class GenericModifyInput<E extends EntityDefinition> {

  /**
   * The payload of the modification input.
   */
  TableData<E> tableData;

  /**
   * The data columns of the {@link #tableData} that are included into the modification in a
   * definite order.
   */
  List<DataColumn<?>> columns = null;

  /**
   * The properties of the {@link #entityDefinition} in the same order than the {@link #columns}
   * list.
   */
  List<PropertyOwned<?>> properties = null;

  /**
   * The idColumns of the {@link #tableData}.
   */
  List<DataColumn<?>> idColumns = null;

  /**
   * The identifier properties of the {@link #entityDefinition} in the same order than in the
   * {@link #idColumns} list.
   */
  List<PropertyOwned<?>> idProperties = null;

  /**
   * The iterator of the {@link #tableData}.
   */
  ListIterator<DataRow> iterator;

  /**
   * The row of the {@link #tableData}
   */
  DataRow row;

  /**
   * The {@link EntityDefinition} then will be modified.
   */
  E entityDefinition;

  /**
   * @param tableData The table data of the modification input.
   */
  public GenericModifyInput(TableData<E> tableData) {
    super();
    this.tableData = tableData;
    this.entityDefinition = tableData.entity();
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

  /**
   * @return The {@link EntityDefinition} the modification input belong to.
   */
  public final E getEntityDefinition() {
    return entityDefinition;
  }

}
