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

import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;

/**
 * Unique index with one column.
 * 
 * @author Peter Boros
 */
public class UniqueIndex<T> extends TableDataIndex {

  /**
   * If the given column requires a fast access to the row and the column value is unique then this
   * map will be maintained to ensure. If the given column is indexed in this way then we can use
   * this index for filtering and accessing the relevant rows by the value of this column.
   */
  private Map<T, DataRow> index = null;

  UniqueIndex(TableData<?> tableData, Property<T> property) {
    super(tableData, IndexType.UNIQUE, false);
    DataColumn<T> column = tableData.getColumn(property);
    init(tableData, column);
  }

  UniqueIndex(TableData<?> tableData, DataColumn<T> column) {
    super(tableData, IndexType.UNIQUE, false);
    init(tableData, column);
  }

  private void init(TableData<?> tableData, DataColumn<T> column) {
    index =
        tableData.rows().stream()
            .collect(Collectors.toMap(r -> tableData.get(column, r), r -> r));
  }

  public DataRow get(T value) {
    return index.get(value);
  }

  public DataRow getObject(Object value) {
    return index.get(value);
  }

}
