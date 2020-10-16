package org.smartbit4all.domain.data;

import org.smartbit4all.domain.meta.Property;

/**
 * The basic building block of the entity data table. It has specific sub classes for accessing the
 * stored, computed and the referred attributes.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public abstract class DataColumn<T> {

  /**
   * The property definition reference.
   */
  private final Property<T> property;

  /**
   * The data column must refer to the owned column that finally contains its value. If it's already
   * an owned column then it's the this reference. If we have a {@link DataColumnRef} then this is
   * the reference of the final column at the end of the join path.
   */
  private DataColumnOwned<T> ownedColumn;

  /**
   * Constructs a data column at abstract level. It contains the {@link Property} from the meta
   * represented by this column.
   * 
   * @param property
   */
  public DataColumn(Property<T> property) {
    super();
    this.property = property;
  }

  /**
   * The generic value getter function.
   * 
   * @param row The row of the value.
   * @return
   */
  abstract T getValue(DataRow row);

  /**
   * Set the value for the given row. It's behavior depends on the sub type. In case of owned column
   * {@link DataColumnOwned} it sets the value directly by the {@link DataRow#getRowDataIndex()} of
   * the row parameter. If it's a {@link DataColumnRef} then it follows the
   * {@link DataColumnRef#referencePath} till the end of the path and set the value of the final
   * owned column.
   * 
   * @param row The row reference.
   * @param value The value to set.
   */
  abstract void setValue(DataRow row, Object value);

  /**
   * The name of the given column. If it's an owned column then the name of the property. In case of
   * a referring column it's the name of the whole join path plus the name of the final property.
   * 
   * @return
   */
  public final String getName() {
    return property.getName();
  }

  /**
   * The property represented by the column.
   * 
   * @return
   */
  public Property<T> getProperty() {
    return property;
  }

  /**
   * Retrieves the {@link #ownedColumn}.
   * 
   * @return
   */
  DataColumnOwned<T> getOwnedColumn() {
    return ownedColumn;
  }

  /**
   * API level function that can set the {@link #ownedColumn}.
   * 
   * @param ownedColumn
   */
  void setOwnedColumn(DataColumnOwned<T> ownedColumn) {
    this.ownedColumn = ownedColumn;
  }

}
