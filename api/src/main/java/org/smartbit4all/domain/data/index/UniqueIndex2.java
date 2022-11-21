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
public class UniqueIndex2<T1 extends Comparable<?>, T2 extends Comparable<?>>
    extends CompositeUniqueIndex {

  UniqueIndex2(TableData<?> tableData, Property<T1> property1, Property<T2> property2) {
    super(tableData);
    DataColumn<T1> column1 = tableData.getColumn(property1);
    DataColumn<T2> column2 = tableData.getColumn(property2);
    init(tableData, column1, column2);
  }

  UniqueIndex2(TableData<?> tableData, DataColumn<T1> column1, DataColumn<T2> column2) {
    super(tableData);
    init(tableData, column1, column2);
  }

  private void init(TableData<?> tableData, DataColumn<T1> column1, DataColumn<T2> column2) {
    index =
        tableData.rows().stream().collect(Collectors.toMap(
            r -> new CompositeValue(tableData.get(column1, r), tableData.get(column2, r)), r -> r));
  }

  public DataRow get(T1 value1, T2 value2) {
    return index.get(new CompositeValue(value1, value2));
  }

  public DataRow getObjects(Object value1, Object value2) {
    return index.get(new CompositeValue((Comparable<?>) value1, (Comparable<?>) value2));
  }

}
