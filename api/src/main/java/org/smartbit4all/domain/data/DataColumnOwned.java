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
package org.smartbit4all.domain.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyComputed;
import org.smartbit4all.domain.meta.PropertyOwned;

/**
 * In case of the stored column the values are stored in a {@link List}. In this case the rows of
 * the given {@link TableData} has the index for this {@link #values} list.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public final class DataColumnOwned<T> extends DataColumn<T> {

  /**
   * The values of the given column. The list itself could differ from the logical data structure.
   * There can be unused elements and this information is not available at this level. By default
   * the values is an empty list and will be created when the first row is added.
   */
  private List<T> values = Collections.emptyList();

  /**
   * Constructs a data column with values.
   * 
   * @param property The property can be {@link PropertyOwned} or {@link PropertyComputed}.
   */
  DataColumnOwned(Property<T> property) {
    super(property);
    setOwnedColumn(this);
  }

  final List<T> values() {
    return values;
  }

  @Override
  T getValue(DataRow row) {
    return values.get(row.getRowDataIndex());
  }

  /**
   * Set the value for the given row.
   * 
   * @param row The row reference.
   * @param value The value to set.
   */
  @Override
  @SuppressWarnings("unchecked")
  void setValue(DataRow row, Object value) {
    if (value != null && !getProperty().type().isInstance(value)) {
      throw new IllegalArgumentException(
          "The given value " + value.getClass().getName() + " can not be set as a "
              + getProperty().type() + " for the " + getProperty().getName() + " property.");
    }
    int rowDataIndex = row.getRowDataIndex();
    while (values.size() - 1 < rowDataIndex) {
      values.add(null);
    }
    values.set(rowDataIndex, (T) value);
  }

  void ensureCapacity(int size) {
    if (values.isEmpty()) {
      values = new ArrayList<>(size);
    }
  }

}
