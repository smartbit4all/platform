package org.smartbit4all.domain.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertySet;

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
   * @param properties The properties that will be initialized as columns in the table data <u>in an
   *        ordered implementation of {@link Collection}</u>
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

}
