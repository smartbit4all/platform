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
package org.smartbit4all.domain.data.index;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;

/**
 * @author Peter Boros
 *
 */
public class NonUniqueIndex<T> extends TableDataIndex {

  /**
   * If the given column requires a fast access to the row and the column value is unique then this
   * map will be maintained to ensure. If the given column is indexed in this way then we can use
   * this index for filtering and accessing the relevant rows by the value of this column.
   */
  private Map<T, List<DataRow>> index = null;

  private List<DataRow> nullRows = null;

  /**
   * The reference for the table data column.
   */
  private WeakReference<DataColumn<T>> columnRef;

  NonUniqueIndex(TableData<?> tableData, Property<T> property) {
    super(tableData, IndexType.NONUNIQUE, false);
    DataColumn<T> column = tableData.getColumn(property);
    columnRef = new WeakReference<DataColumn<T>>(column);
    init(tableData, column);
  }

  NonUniqueIndex(TableData<?> tableData, DataColumn<T> column) {
    super(tableData, IndexType.NONUNIQUE, false);
    columnRef = new WeakReference<DataColumn<T>>(column);
    init(tableData, column);
  }

  private void init(TableData<?> tableData, DataColumn<T> column) {
    index = new HashMap<T, List<DataRow>>();
    nullRows = new ArrayList<DataRow>();

    nullRows = tableData.rows().stream()
        .filter(r -> tableData.get(column, r) == null)
        .collect(Collectors.toList());
    
    index = tableData.rows().stream()
        .filter(r -> !nullRows.contains(r))
        .collect(Collectors.groupingBy(r -> tableData.get(column, r)));
  }

  public List<DataRow> get(T value) {
    if(value == null) {
      return new ArrayList<>(nullRows);
    }
    
    List<DataRow> indexValues = index.get(value);
    if(indexValues == null || indexValues.size() < 1) {
      return Collections.emptyList();
    }
    
    return new ArrayList<>(indexValues);
  }

}
