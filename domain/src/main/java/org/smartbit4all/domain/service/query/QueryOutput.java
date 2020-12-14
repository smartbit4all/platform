/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.service.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.index.UniqueIndex;
import org.smartbit4all.domain.data.index.UniqueIndex2;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertySet;

public class QueryOutput<E extends EntityDefinition> {

  enum QueryOutputMode {
    INTO, APPEND, MERGE
  }

  /**
   * The existing or the newly created {@link TableData}.
   */
  TableData<E> tableData;

  /**
   * Temporary tableData created for merge operation.
   */
  TableData<E> tempData;

  /**
   * The current row used for setting the values.
   */
  DataRow row;

  /**
   * The {@link EntityDefinition} the query is based on.
   */
  E entityDef;

  QueryOutputMode queryOutputMode = QueryOutputMode.INTO;

  /**
   * The index based access to the {@link DataColumn}.
   */
  List<DataColumn<?>> columnsByIndex = new ArrayList<>();

  public void into(TableData<E> result) {
    this.tableData = result;
    this.entityDef = result.entity();
    this.queryOutputMode = QueryOutputMode.INTO;
  }

  public void append(TableData<E> result) {
    this.tableData = result;
    this.entityDef = result.entity();
    this.queryOutputMode = QueryOutputMode.APPEND;
  }

  public void merge(TableData<E> result) {
    this.tableData = result;
    this.entityDef = result.entity();
    this.queryOutputMode = QueryOutputMode.MERGE;
  }

  public TableData<E> result() {
    return tableData;
  }

  public static final int COLUMNNOTACCEPTED = -1;

  private UniqueIndex2<? extends Comparable<?>, ? extends Comparable<?>> pkIndex2;

  private UniqueIndex<? extends Comparable<?>> pkIndex;

  private Property<? extends Comparable<?>> primaryKey;

  private Property<? extends Comparable<?>> primaryKey2;

  /**
   * A call back function to notify that the fetch began.
   */
  public void start() {
    if (tableData == null) {
      tableData = new TableData<>(entityDef);
    }
    if (queryOutputMode == QueryOutputMode.INTO) {
      tableData.clearRows();
    }
    if (queryOutputMode == QueryOutputMode.MERGE) {
      // create temp tabledata for holding temporary information
      tempData = new TableData<>(tableData.entity());
      tempData.addColumns(tableData.properties().toArray(new Property<?>[0]));
      // create pk indexes
      PropertySet primaryKeys = entityDef.PRIMARYKEYDEF();
      if (primaryKeys.size() > 2) {
        // TODO use common exception message for related restrictions!
        throw new RuntimeException("More than 2 primary keys not supported!");
      }
      Iterator<Property<?>> pk = primaryKeys.iterator();
      primaryKey = (Property<? extends Comparable<?>>) pk.next();
      if (tableData.properties().contains(primaryKey)) {
        throw new RuntimeException(
            "TableData doesn't contain primaryKey (" + primaryKey.getName() + ") when merging!");
      }
      // pkIndex or pkIndex2 will be set, the other will be null
      if (pk.hasNext()) {
        primaryKey2 = (Property<? extends Comparable<?>>) pk.next();
        if (tableData.properties().contains(primaryKey2)) {
          throw new RuntimeException("TableData doesn't contain primaryKey2 ("
              + primaryKey2.getName() + ") when merging!");
        }
        pkIndex2 = tableData.index().unique(primaryKey, primaryKey2);
      } else {
        pkIndex = tableData.index().unique(primaryKey);
      }
    }
  }

  /**
   * This ensure the given result that it will accept the given {@link Property} as a fetch result.
   * 
   * @param property The property to accept.
   * @return The index that can be used to set the value of the result. If it's
   *         {@link #COLUMNNOTACCEPTED} then there is no room for the given property in the result.
   */
  public int accept(Property<?> property) {
    if (queryOutputMode == QueryOutputMode.MERGE) {
      tempData.addColumnOwn(property);
      // make sure target table data will also contain this property
      tableData.addColumnOwn(property);
    } else {
      columnsByIndex.add(tableData.addColumnOwn(property));
    }
    return columnsByIndex.size() - 1;
  }

  /**
   * The {@link QueryResult} acts like an iterator for the fetch result. This function set the
   * previously {@link #accept(Property)} property by index.
   * 
   * @param index The index for the {@link Property} generated by the {@link #accept(Property)}
   *        method.
   * @param value The value to set.
   */
  public void setValue(int index, Object value) {
    if (queryOutputMode == QueryOutputMode.MERGE) {
      tempData.setObject(columnsByIndex.get(index), row, value);
    } else {
      tableData.setObject(columnsByIndex.get(index), row, value);
    }
  }

  /**
   * Adding a new row for the result. The new row will be the iterated so the next
   * {@link #setValue(int, Object)} will work on this.
   * 
   * @return Return true if the result can accept the next line. If it's false then the
   *         {@link Query} will skip the rest of the result and stop fetching.
   */
  public boolean startRow() {
    if (queryOutputMode == QueryOutputMode.MERGE) {
      row = tempData.addRow();
    } else {
      row = tableData.addRow();
    }
    // TODO manage limit!
    return true;
  }

  /**
   * The query will call back this function when the reading from the data source is ready and all
   * the stored properties are set. At this event the result can filter, compute or do some other
   * post processing.
   */
  public void finishRow() {
    if (queryOutputMode == QueryOutputMode.MERGE) {
      DataRow targetRow = null;
      if (pkIndex != null) {
        targetRow = pkIndex.getObject(row.get(primaryKey));
      } else {
        targetRow = pkIndex2.getObjects(row.get(primaryKey), row.get(primaryKey2));
      }
      if (targetRow == null) {
        // TODO not found in tableData --> add new row or ignore?
        targetRow = tableData.addRow();
      }
      for (Property<?> prop : tableData.properties()) {
        targetRow.setObject(prop, row.get(prop));
      }
      tempData.clearRows();
    }
    row = null;
  }

  /**
   * When the query is over and every accessible record is fetched or at least the fetch is stopped
   * with any other reason then this function is called. It's a notification that the post process
   * can start.
   */
  public void finish() {
    tempData = null;
  }

  @Override
  public String toString() {
    return tableData.toString();
  }

}
