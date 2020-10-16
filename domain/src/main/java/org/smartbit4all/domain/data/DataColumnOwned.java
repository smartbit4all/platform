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
  @SuppressWarnings("unchecked")
  void setValue(DataRow row, Object value) {
    if(value!= null && !this.getProperty().type().isInstance(value)) {
      throw new IllegalArgumentException("The given value can not be set as a " + this.getProperty().type() + ".");
    }
    values.set(row.getRowDataIndex(), (T) value);
  }

  void ensureCapacity(int size) {
    if (values.isEmpty()) {
      values = new ArrayList<T>(size);
    }
  }

}
