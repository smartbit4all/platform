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
package org.smartbit4all.domain.data.index;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.utility.CompositeValue;

/**
 * Composite unique index with two column.
 * 
 * @author Peter Boros
 */
public class NonUniqueIndex2<T1 extends Comparable<?>, T2 extends Comparable<?>>
    extends CompositeNonUniqueIndex {

  /**
   * The reference for the table data column.
   */
  private WeakReference<DataColumn<T1>> columnRef1;

  /**
   * The reference for the table data column.
   */
  private WeakReference<DataColumn<T2>> columnRef2;

  NonUniqueIndex2(TableData<?> tableData, Property<T1> property1, Property<T2> property2) {
    super(tableData);
    DataColumn<T1> column1 = tableData.getColumn(property1);
    columnRef1 = new WeakReference<DataColumn<T1>>(column1);
    DataColumn<T2> column2 = tableData.getColumn(property2);
    columnRef2 = new WeakReference<DataColumn<T2>>(column2);
    init(tableData, column1, column2);
  }

  NonUniqueIndex2(TableData<?> tableData, DataColumn<T1> column1, DataColumn<T2> column2) {
    super(tableData);
    columnRef1 = new WeakReference<DataColumn<T1>>(column1);
    columnRef2 = new WeakReference<DataColumn<T2>>(column2);
    init(tableData, column1, column2);
  }

  private void init(TableData<?> tableData, DataColumn<T1> column1, DataColumn<T2> column2) {
    index =
        tableData.rows().stream().collect(Collectors.groupingBy(
            r -> new CompositeValue(tableData.get(column1, r), tableData.get(column2, r))));
  }

  public List<DataRow> get(T1 value1, T2 value2) {
    return index.get(new CompositeValue(value1, value2));
  }

}
