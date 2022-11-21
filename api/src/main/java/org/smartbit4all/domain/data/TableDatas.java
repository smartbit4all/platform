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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertySet;
import org.springframework.util.ObjectUtils;

/**
 * Companion class of {@link TableData}
 */
public final class TableDatas {

  static final String ACTION = "ACTION";
  static final String separator = StringConstant.COMMA;

  /**
   * Unable to initiate. Use only as static.
   */
  private TableDatas() {
    super();
  }

  /**
   * Appends the otherTableDatas to the baseTableData. When a column is missing from the base, it
   * will be added.
   * 
   * @param baseTableData the target table data which will be extended
   * @param otherTableDatas the TableDatas which will be added to the base
   * @return The newly created rows of the baseTableData.
   */
  public static List<DataRow> append(TableData<?> baseTableData, TableData<?>... otherTableDatas) {
    List<DataRow> results = new ArrayList<>();
    if (otherTableDatas == null) {
      return results;
    }
    for (TableData<?> otherTableData : otherTableDatas) {
      if (otherTableData != null) {
        Map<DataColumn<?>, DataColumn<?>> colMap = new HashMap<>();
        for (DataColumn<?> col : otherTableData.columns()) {
          Property<?> property = col.getProperty();
          DataColumn<?> baseCol = baseTableData.getColumn(property);
          if (baseCol == null) {
            baseCol = baseTableData.addColumnOwn(property);
          }
          colMap.put(col, baseCol);
        }
        if (colMap.isEmpty()) {
          continue;
        }
        for (DataRow row : otherTableData.rows()) {
          DataRow newRow = baseTableData.addRow();
          results.add(newRow);
          for (Entry<DataColumn<?>, DataColumn<?>> entry : colMap.entrySet()) {
            newRow.setObject(entry.getValue(), entry.getKey().getValue(row));
          }
        }
      }
    }
    return results;
  }

  public static TableData<?> merge(PropertySet keyProperties,
      TableData<?> baseTableData, TableData<?>... otherTableDatas) {
    // TODO implement merge
    throw new UnsupportedOperationException("Not implemented");
  }

  public static TableData<?> merge(TableData<?> baseTableData, TableData<?>... otherTableDatas) {
    // TODO implement merge
    throw new UnsupportedOperationException("Not implemented");
  }

  static final String toString(TableData<?> dt) {
    // Estimate the size of one cell with 20 bytes.
    StringBuffer sb = new StringBuffer(dt.size() * dt.columnMap.size() * 20);
    // Print out the columns.
    boolean firstColumn = true;
    for (DataColumn<?> column : dt.columns()) {
      if (!firstColumn) {
        sb.append(separator);
      }
      sb.append(column.getName());
      firstColumn = false;
    }

    // Print out the data.
    for (DataRow row : dt.rows()) {
      sb.append(StringConstant.NEW_LINE);
      firstColumn = true;
      for (DataColumn<?> column : dt.columns()) {
        if (!firstColumn) {
          sb.append(separator);
        }
        sb.append(dt.get(column, row));
        firstColumn = false;
      }
    }

    return sb.toString();
  }

  public static final String toStringAdv(TableData<?> dt) {

    Map<String, Integer> colWithByColName = new HashMap<>();
    Collection<DataColumn<?>> columns = dt.columns().stream()
        .sorted(Comparator.comparing(col -> col.getName())).collect(Collectors.toList());
    for (DataColumn<?> column : columns) {
      String colName = column.getName();
      int length = colName.length();
      for (DataRow row : dt.rows()) {
        Object rowValue = dt.get(column, row);
        int rowValueLength = 4;
        if (rowValue != null) {
          rowValueLength = rowValue.toString().length();
        }
        length = length < rowValueLength ? rowValueLength : length;
      }

      colWithByColName.put(colName, length);
    }

    StringBuilder hrBuilder = new StringBuilder();
    long fullWidth =
        colWithByColName.values().stream().collect(Collectors.summarizingInt(i -> i + 3)).getSum();
    for (int i = 0; i < fullWidth; i++) {
      hrBuilder.append("-");
    }

    String hr = hrBuilder.toString();

    // Estimate the size of one cell with 20 bytes.
    StringBuffer sb = new StringBuffer(dt.size() * dt.columnMap.size() * 20);
    sb.append(hr).append(StringConstant.NEW_LINE);
    // Print out the columns.
    boolean firstColumn = true;
    for (DataColumn<?> column : columns) {
      if (!firstColumn) {
        sb.append("|");
      }
      String colName = column.getName();
      sb.append(" ");
      sb.append(colName);
      int colWidth = colWithByColName.get(column.getName());
      String fill = createFillSpace(colName, colWidth);
      sb.append(fill);
      sb.append(" ");
      firstColumn = false;
    }

    sb.append(StringConstant.NEW_LINE);
    sb.append(hr);

    // Print out the data.
    for (DataRow row : dt.rows()) {
      sb.append(StringConstant.NEW_LINE);
      firstColumn = true;
      for (DataColumn<?> column : columns) {
        if (!firstColumn) {
          sb.append("|");
        }
        Object value = dt.get(column, row);
        String valueToWrite = "null";
        if (value != null) {
          valueToWrite = value.toString();
        }
        sb.append(" ");
        sb.append(valueToWrite);
        int colWidth = colWithByColName.get(column.getName());
        String fill = createFillSpace(valueToWrite, colWidth);
        sb.append(fill);
        sb.append(" ");
        firstColumn = false;
      }
    }

    sb.append(StringConstant.NEW_LINE);
    sb.append(hr);

    return sb.toString();
  }

  private static String createFillSpace(String valueToWrite, int colWidth) {
    int spaceNum = colWidth - valueToWrite.length();
    StringBuilder fillBuilder = new StringBuilder();
    for (int i = 0; i < spaceNum; i++) {
      fillBuilder.append(" ");
    }
    String fill = fillBuilder.toString();
    return fill;
  }

  /**
   * Sorts the rows of the given {@link TableData} by the given {@link SortProperty}-ies.</br>
   * 
   * @param <E> The {@link EntityDefinition} of the {@link TableData}
   * @param tableData The source {@link TableData}
   * @param sortProperties The properties describing the sort orders
   */
  public static <E extends EntityDefinition> void sort(TableData<E> tableData,
      List<SortProperty> sortProperties) {
    Objects.requireNonNull(tableData, "tableData can not be null!");
    if (ObjectUtils.isEmpty(sortProperties)) {
      throw new IllegalArgumentException("sortProperties can not be null nor empty!");
    }

    // check the sortProperties
    for (SortProperty sortProp : sortProperties) {
      if (tableData.getColumn(sortProp.getProperty()) == null) {
        throw new IllegalArgumentException(
            "The given TableData has no property with the descibed SortProperty: ["
                + sortProp.getProperty().getUri() + "]!");
      }
    }

    sortRows(tableData.rowModel.rows, sortProperties);
  }

  /**
   * Creates a new {@link TableData} that is sorted by the given {@link SortProperty}-ies.</br>
   * The source and the result TableData both will exist in the memory, so be aware of the double
   * memory consumption.
   * 
   * @param <E> The {@link EntityDefinition} of the {@link TableData}
   * @param tableData The source {@link TableData}
   * @param sortProperties The properties describing the sort orders
   * @return The new sorted {@link TableData}
   */
  public static <E extends EntityDefinition> TableData<E> sortToNew(TableData<E> tableData,
      List<SortProperty> sortProperties) {
    Objects.requireNonNull(tableData, "tableData can not be null!");
    if (ObjectUtils.isEmpty(sortProperties)) {
      throw new IllegalArgumentException("sortProperties can not be null nor empty!");
    }

    // check the sortProperties
    for (SortProperty sortProp : sortProperties) {
      if (tableData.getColumn(sortProp.getProperty()) == null) {
        throw new IllegalArgumentException(
            "The given TableData has no property with the descibed SortProperty: ["
                + sortProp.getProperty().getUri() + "]!");
      }
    }

    TableData<E> result = copy(tableData);
    sortRows(result.rowModel.rows, sortProperties);

    return result;
  }

  private static void sortRows(List<DataRow> rows, List<SortProperty> sortProperties) {
    Collections.sort(rows, getDataRowComparator(sortProperties));
  }

  public static Comparator<DataRow> getDataRowComparator(List<SortProperty> sortProperties) {
    return (row1, row2) -> {
      for (SortProperty sortProp : sortProperties) {
        Property<?> prop = sortProp.getProperty();
        Comparator<Object> comparator = (Comparator<Object>) prop.getComparator();
        if (comparator == null) {
          return 0;
        }
        int res = comparator.compare(row1.get(prop), row2.get(prop));
        if (res != 0) {
          return sortProp.isAscending() ? res : res * -1;
        }
      }

      return 0;
    };
  }

  /**
   * Returns a new empty {@link TableData} with the columns of the given source.
   */
  public static <E extends EntityDefinition> TableData<E> copyMeta(TableData<E> sourceTableData) {
    return of(sourceTableData.entity(), sourceTableData.properties());
  }

  /**
   * Returns a new copy instance of the given {@link TableData}
   */
  public static <E extends EntityDefinition> TableData<E> copy(TableData<E> sourceTableData) {
    return copyRows(sourceTableData, sourceTableData.rows());
  }

  /**
   * Returns a new copy instance of the given {@link TableData} with the given rows.
   * 
   * @param rows The rows to copy.
   */
  public static <E extends EntityDefinition> TableData<E> copyRows(TableData<E> sourceTableData,
      List<DataRow> rows) {
    TableData<E> copy = copyMeta(sourceTableData);
    for (DataRow row : rows) {
      DataRow newRow = copy.addRow();
      for (Property<?> prop : sourceTableData.properties()) {
        newRow.setObject(prop, row.get(prop));
      }
    }
    return copy;
  }

  /**
   * Creates a new instance of {@link TableData} with the given {@link EntityDefinition}.
   * 
   * @return A new instance of {@link TableData}
   */
  public static final <T extends EntityDefinition> TableData<T> of(T entityDef) {
    return new TableData<>(entityDef);
  }

  /**
   * Creates a new instance of {@link TableData} with the properties of the given
   * {@link PropertySet}.
   * 
   * @param <T> The subtype (subinterface) of {@link EntityDefinition} that is used by the table
   *        data.
   * @param entityDef The object of the entity definition that is used by the table data.
   * @param propertySet The properties that will be initialized as columns in the table data.
   * @return A new instance of {@link TableData}
   */
  public static final <T extends EntityDefinition> TableData<T> of(T entityDef,
      PropertySet propertySet) {
    return builder(entityDef, propertySet).build();
  }

  /**
   * Creates a new instance of {@link TableData} with the properties of the given
   * {@link PropertySet}.
   * 
   * @param <T> The subtype (subinterface) of {@link EntityDefinition} that is used by the table
   *        data.
   * @param entityDef The object of the entity definition that is used by the table data.
   * @param properties The properties that will be initialized as columns in the table data.
   * @return A new instance of {@link TableData}
   */
  public static final <T extends EntityDefinition> TableData<T> of(T entityDef,
      Property<?>... properties) {
    return builder(entityDef, properties).build();
  }

  /**
   * Creates a new instance of {@link TableData} with the properties of the given
   * {@link PropertySet}.
   * 
   * @param <T> The subtype (subinterface) of {@link EntityDefinition} that is used by the table
   *        data.
   * @param entityDef The object of the entity definition that is used by the table data.
   * @param properties The properties that will be initialized as columns in the table data.
   * @return A new instance of {@link TableData}
   */
  public static final <T extends EntityDefinition> TableData<T> of(T entityDef,
      Collection<Property<?>> properties) {
    return builder(entityDef, properties).build();
  }

  /**
   * Creates a {@link Builder} that can construct a new {@link TableData} instance with data
   * represented in its rows.
   * <p>
   * The built {@link TableData} will contain the properties given in the parameters as columns.
   * This set of properties can not be extended with the building methods!
   * 
   * @param <T> The subtype (subinterface) of {@link EntityDefinition} that is used by the table
   *        data.
   * @param entityDef The object of the entity definition that is used by the table data.
   * @param properties The properties that will be initialized as columns in the table data.
   * @return A new instance of {@link BuilderWithFixProperties}
   */
  public static <T extends EntityDefinition> BuilderWithFixProperties<T> builder(T entityDef,
      Property<?>... properties) {
    return builder(entityDef, Arrays.asList(properties));
  }

  /**
   * Creates a {@link Builder} that can construct a new {@link TableData} instance with data
   * represented in its rows.
   * <p>
   * The built {@link TableData} will contain the properties given in the parameters as columns.
   * This set of properties can not be extended with the building methods!
   * 
   * @param <T> The subtype (subinterface) of {@link EntityDefinition} that is used by the table
   *        data.
   * @param entityDef The object of the entity definition that is used by the table data.
   * @param propertySet The properties that will be initialized as columns in the table data.
   * @return A new instance of {@link BuilderWithFixProperties}
   */
  public static <T extends EntityDefinition> BuilderWithFixProperties<T> builder(T entityDef,
      PropertySet propertySet) {
    return builder(entityDef, propertySet.toArray(new Property<?>[0]));
  }

  /**
   * Creates a {@link Builder} that can construct a new {@link TableData} instance with data
   * represented in its rows.
   * <p>
   * The built {@link TableData} will contain the properties given in the parameters as columns.
   * This set of properties can not be extended with the building methods!
   * 
   * @param <T> The subtype (subinterface) of {@link EntityDefinition} that is used by the table
   *        data.
   * @param entityDef The object of the entity definition that is used by the table data.
   * @param properties The properties that will be initialized as columns in the table data.
   * @return A new instance of {@link BuilderWithFixProperties}
   */
  public static <T extends EntityDefinition> BuilderWithFixProperties<T> builder(T entityDef,
      Collection<Property<?>> properties) {
    return new BuilderWithFixProperties<>(entityDef, properties);
  }

  /**
   * Creates a {@link Builder} that can construct a new {@link TableData} instance with data
   * represented in its rows.
   * <p>
   * The set of columns of the built {@link TableData} will be implicitly extended as the values are
   * set to the new rows.
   * 
   * @param <T> The subtype (subinterface) of {@link EntityDefinition} that is used by the table
   *        data.
   * @param entityDef The object of the entity definition that is used by the table data.
   * @return A new instance of {@link BuilderWithFlexibleProperties}
   */
  public static <T extends EntityDefinition> BuilderWithFlexibleProperties<T> builder(T entityDef) {
    return new BuilderWithFlexibleProperties<>(entityDef);
  }

  /**
   * Creates a {@link Builder} that can construct a new {@link TableData} instance with data
   * represented in its rows.
   * <p>
   * The built {@link TableData} will contain the properties given in the parameters as columns.
   * This set of properties can not be extended with the building methods!
   * <p>
   * <b><i>Important!</i></b> To use the {@link BuilderWithOrderedProperties#setValues(Object...)
   * setValues()} method, the parameter properties must be an ordered implementation of
   * {@link Collection}, like {@link ArrayList} or {@link LinkedHashSet}!
   * 
   * @param <T> The subtype (subinterface) of {@link EntityDefinition} that is used by the table
   *        data.
   * @param entityDef The object of the entity definition that is used by the table data.
   * @param orderedProperties The properties that will be initialized as columns in the table data
   *        <u>in an ordered implementation of {@link Collection}</u>
   * @return A new instance of {@link BuilderWithOrderedProperties}
   */
  public static <T extends EntityDefinition> BuilderWithOrderedProperties<T> builderWithOrderedProperties(
      T entityDef, Collection<Property<?>> orderedProperties) {
    return new BuilderWithOrderedProperties<>(entityDef, orderedProperties);
  }

  public static abstract class Builder<T extends Builder<T, E>, E extends EntityDefinition> {

    protected E entityDef;
    protected Set<Property<?>> properties;
    protected ArrayList<Map<Property<?>, Object>> rows;

    protected Builder(E entityDef) {
      this.entityDef = Objects.requireNonNull(entityDef,
          "Builder can not be initialized with null entityDef parameter!");
      this.properties = new HashSet<>();
      rows = new ArrayList<>();
    }

    protected abstract T self();

    /**
     * Builds a new instance of {@link TableData} with the given options of the builder.
     * 
     * @return new instance of {@link TableData}
     */
    public TableData<E> build() {
      TableData<E> tableData = of(entityDef);
      for (Property<?> property : properties) {
        tableData.addColumn(property);
      }
      for (Map<Property<?>, Object> rowData : rows) {
        DataRow newRow = tableData.addRow();
        for (Entry<Property<?>, Object> entry : rowData.entrySet()) {
          newRow.setObject(entry.getKey(), entry.getValue());
        }
      }
      return tableData;
    }

    /**
     * The number of rows in the builder.
     * 
     * @return
     */
    public int rowSize() {
      return rows.size();
    }

    /**
     * Returns true if there is no row in the builder.
     * 
     * @return
     */
    public boolean isEmpty() {
      return rows.isEmpty();
    }

    /**
     * Adds a new row that can be filled with data with the {@link #set(Property, Object) set()}
     * method.
     */
    public T addRow() {
      rows.add(new HashMap<>());
      return self();
    }

    /**
     * Sets the value of the given property on the last added row.
     * <p>
     * {@link IllegalStateException} is thrown if there is no previously added row on the builder.
     * </br>
     * {@link IllegalArgumentException} is thrown if the builder holds a predefined property set and
     * it does not contain the currently set property.
     * 
     * @param property The property of the current row that will be set
     * @param value The value to set
     * @param <P> the exact type that the property handles
     * 
     * @throws IllegalStateException If there is no previously added row on the builder
     * @throws IllegalArgumentException If the builder holds a predefined property set and it does
     *         not contain the currently set property
     */
    public <P> T set(Property<P> property, P value) {
      lastRow().put(property, value);
      return self();
    }

    /**
     * Sets the value of the given property on the last added row, if the value is not null.
     * 
     * <br/>
     * <br/>
     * 
     * <b>Warning!</b> If the column added to the TableData in other way (eg. in other row), this
     * value is going to be null, despite the value is not set in this call!
     * 
     * @param <P> the exact type that the property handles
     * @param property The property of the current row that will be set
     * @param value The value to set
     */
    public <P> T setNotNull(Property<P> property, P value) {
      if (value != null) {
        return set(property, value);
      } else {
        return self();
      }
    }

    protected final Map<Property<?>, Object> lastRow() {
      if (rows.isEmpty()) {
        throw new IllegalStateException(
            "There are no rows added to the TableData builder! Make sure to call addRow()!");
      }
      return rows.get(rows.size() - 1);
    }

    protected final void checkProperty(Property<?> property) {
      if (!property.getEntityDef().entityDefName().equals(this.entityDef.entityDefName())) {
        throw new IllegalArgumentException(
            "The given collection of properties contains element(s) that doesn't belong to the"
                + "given entityDefinition!");
      }
    }

  }

  public static class BuilderWithFlexibleProperties<E extends EntityDefinition>
      extends Builder<BuilderWithFlexibleProperties<E>, E> {

    protected BuilderWithFlexibleProperties(E entityDef) {
      super(entityDef);
    }

    @Override
    public <P> BuilderWithFlexibleProperties<E> set(Property<P> property, P value) {
      if (!properties.contains(property)) {
        checkProperty(property);
        properties.add(property);
      }
      return super.set(property, value);
    }

    @Override
    protected BuilderWithFlexibleProperties<E> self() {
      return this;
    }

  }

  public static class BuilderWithFixProperties<E extends EntityDefinition>
      extends Builder<BuilderWithFixProperties<E>, E> {

    protected BuilderWithFixProperties(E entityDef, Collection<Property<?>> properties) {
      super(entityDef);
      initPropertySet(properties);
    }

    protected void initPropertySet(Collection<Property<?>> properties) {
      Objects.requireNonNull(properties);
      checkProperties(properties);
      this.properties = Collections.unmodifiableSet(new HashSet<>(properties));
    }

    protected void checkProperties(Collection<Property<?>> properties) {
      for (Property<?> property : properties) {
        checkProperty(property);
      }
    }

    @Override
    public <P> BuilderWithFixProperties<E> set(Property<P> property, P value) {
      if (!properties.contains(property)) {
        throw new IllegalArgumentException(
            "The given property to set is not declared in the builder!");
      }
      return super.set(property, value);
    }

    @Override
    protected BuilderWithFixProperties<E> self() {
      return this;
    }

  }

  public static class BuilderWithOrderedProperties<E extends EntityDefinition>
      extends BuilderWithFixProperties<E> {

    protected BuilderWithOrderedProperties(E entityDef, Collection<Property<?>> properties) {
      super(entityDef, properties);
    }

    @Override
    protected void initPropertySet(Collection<Property<?>> properties) {
      Objects.requireNonNull(properties);
      checkProperties(properties);
      this.properties = new LinkedHashSet<>(properties);
    }

    @Override
    protected BuilderWithOrderedProperties<E> self() {
      return this;
    }

    public BuilderWithOrderedProperties<E> setValues(Object... values) {
      Objects.requireNonNull(values);
      int valueSize = values.length;
      if (valueSize > properties.size()) {
        throw new IllegalArgumentException(
            "There are more objects given to set than the column number of the table!");
      }
      int i = 0;
      for (Property<?> property : properties) {
        Object value = values[i];
        if (!property.type().isInstance(value)) {
          throw new IllegalArgumentException(
              "The type of the value setted on row[" + (this.rows.size() - 1) + "] col[" + i
                  + "] does not match with the corresponding Property type: ["
                  + property.type().getName() + "]!");
        }
        lastRow().put(property, value);
        i++;
        if (i >= valueSize) {
          break;
        }
      }

      return self();
    }

    @Override
    public BuilderWithOrderedProperties<E> addRow() {
      rows.add(new LinkedHashMap<>());
      return self();
    }

  }

  public static class SortProperty {

    private Property<?> property;
    private boolean ascending;

    private SortProperty(Property<?> property, boolean ascending) {
      this.property = property;
      this.ascending = ascending;
    }

    public Property<?> getProperty() {
      return property;
    }

    public boolean isAscending() {
      return ascending;
    }

    public static SortProperty ascending(Property<?> property) {
      return new SortProperty(property, true);
    }

    public static SortProperty descending(Property<?> property) {
      return new SortProperty(property, false);
    }

  }

}
