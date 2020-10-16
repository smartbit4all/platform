package org.smartbit4all.domain.data;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.smartbit4all.domain.data.index.TableDataIndexSet;
import org.smartbit4all.domain.meta.DataConverter;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.Reference.Join;

/**
 * The SmartBit4all basic api is build upon the documented enterprise pattern from Martin Fowler.
 * This approach is published in 2002. The SB4 uses this because it's an effective way of managing
 * the data in the memory. We can avoid creating many-many classes and instances. All the data is
 * stored in typed arrays and the data model is responsible for accessing the data in a structured
 * way. The {@link TableData} is a singleton in a collaboration. But we can have many RecordSet
 * referring the TableData. All these RecordSets are managed and maintained by the TableData event
 * bus.
 * 
 * @see <a href="https://www.martinfowler.com/eaaCatalog/recordSet.html">RecordSet</a>
 * 
 * 
 * @author Peter Boros
 */
public final class TableData<E extends EntityDefinition> {

  /**
   * The {@link EntityDefinition} that defines the content of the entity data table. Used to access
   * the meta data easily.
   */
  private final E entityDef;

  /**
   * These are the references of the data table. Every reference points to neighboring entity. The
   * meta data comes from the definition objects. The same reference is maintained by the referred
   * entity also! It's maintained in the {@link #referredBy} list of the
   * {@link DataReference#target()}. In this map the key is the {@link Reference} that defines the
   * meta data of this reference.
   */
  Map<Reference<E, ?>, DataReference<E, ?>> referenceTo = new LinkedHashMap<>();

  /**
   * This map contains the references points to this data table. The references are maintained on
   * the referrer side by the {@link DataReference} object. It is the instance of the
   * {@link Reference} from the meta data. For the fast access to the referring rows this side
   * maintain the list of rows for every row in this data table.
   * 
   * Let's assume that we have a User, a Role and a UserRole entity in the database. We have two
   * reference UserRole --> User and UserRole --> Role. In the TableData model we will have the two
   * DataReference with the same meta. The DataReference "UserRole --> User will" contain a list of
   * row references for the UserRole rows. It defines the referred User rows for every UserRole row.
   * But at the same time it has a list of row lists that defines every User row the rows of the
   * referencing UserRole rows.
   */
  Map<Reference<?, E>, DataReference<?, E>> referredBy = new LinkedHashMap<>();

  /**
   * The rows of the data table. It contains the valid rows in the proper order. The rows are always
   * points to the given row by their rowIndex. The row index itself is private and can't be seen!
   * These objects are maintained during the modification of the data table.
   */
  final TableDataRowModel rowModel = new TableDataRowModel(this);

  /**
   * The column cache maintained during the construction of the table data. It'a always up-to-date.
   */
  Map<String, DataColumn<?>> columnMap = new LinkedHashMap<>();

  /**
   * These are the bean and the table data structure mappings we currently have. Initially we have
   * no mapper. If we use the {@link TableDataBeanBinding} with a specific bean then this map store
   * the mapper by the class of the bean.
   */
  Map<Class<?>, TableDataBeanBinding> beanMappers = null;

  /**
   * The table data can be indexed by different set of columns. This object is initialized with the
   * first indexedBy() call and later on it will be updated at every change of the rows. The index
   * can make it faster to access the rows by value but on the other hand it slows down the
   * modification of the table data. So be careful about indexing!
   */
  private TableDataIndexSet indexSet = null;

  /**
   * It constructs a new entity data table for the given entity definition.
   * 
   * @param entityDef
   */
  public TableData(E entityDef) {
    super();
    this.entityDef = entityDef;
  }

  /**
   * If we have a stored column then the value can be accesses directly by the row index. Otherwise
   * if we have a referred column then we need to use the reference for the given data table to
   * access the row.
   * 
   * @param <T>
   * @param column
   * @param row
   * @return Returns the raw data from the given cell of the data table. It return the value without
   *         any transformation.
   */
  public <T> T get(DataColumn<T> column, DataRow row) {
    return column.getValue(row);
  }

  public Object[] get(DataRow row, DataColumn<?>... columns) {
    Object result[] = new Object[columns.length];
    for (int i = 0; i < columns.length; i++) {
      result[i] = get(columns[i], row);
    }
    return result;
  }

  /**
   * The values from the given column in an ordered list.
   * 
   * @param <T>
   * @param column The column.
   * @return The list of values from the given column.
   */
  public <T> List<T> values(DataColumn<T> column) {
    if (size() == 0) {
      return Collections.emptyList();
    }
    List<T> result = new ArrayList<>(size());
    for (DataRow row : rows()) {
      result.add(get(column, row));
    }
    return result;
  }

  /**
   * This get can use a conversion for reading the value. This conversion can be provided by the
   * type but also can be defined outside. The only thing to know that you should use the same
   * conversion in the get and in the set.
   * 
   * @param <C> The type of the result.
   * @param <T> The type of the given property that must match with the source type of the
   *        {@link DataConverter} logic.
   * @param column The column.
   * @param row The row.
   * @param converter The conversion logic to transform the raw data.
   * @return The target value produced by the {@link DataConverter#app2ext(Object)}.
   */
  public <C, T> C get(DataColumn<T> column, DataRow row, DataConverter<T, C> converter) {
    return converter.app2ext(column.getValue(row));
  }

  /**
   * If we have a stored column then the value can be accesses directly by the row index. Otherwise
   * if we have a referred column then we need to use the reference for the given data table to
   * access the row.
   * 
   * @param <T>
   * @param column
   * @param row
   * @return Returns the raw data from the given cell of the data table. It return the value without
   *         any transformation.
   */
  public <T> void set(DataColumn<T> column, DataRow row, T value) {
    column.setValue(row, value);
  }

  /**
   * If we have a stored column then the value can be accesses directly by the row index. Otherwise
   * if we have a referred column then we need to use the reference for the given data table to
   * access the row.
   * 
   * @param <T>
   * @param column
   * @param row
   * @return Returns the raw data from the given cell of the data table. It return the value without
   *         any transformation.
   */
  public void setObject(DataColumn<?> column, DataRow row, Object value) {
    column.setValue(row, value);
  }

  /**
   * The size of the data table.
   * 
   * @return This is the size of the {@link TableDataRowModel#rows()}. This list contains the public
   *         visible rows of the data table.
   */
  public int size() {
    return rowModel.rows.size();
  }

  /**
   * Return false if there is at least one row.
   * 
   * @return true if empty.
   */
  public boolean isEmpty() {
    return rowModel.rows.isEmpty();
  }

  @SuppressWarnings("unchecked")
  public <T> DataColumn<T> getColumn(Property<T> property) {
    return (DataColumn<T>) columnMap.get(property.getName());
  }

  /**
   * Add all the columns of the property set if it's not exist already in the table.
   * 
   * @param properties
   * @return
   */
  public Set<DataColumn<?>> addColumns(PropertySet properties) {
    Set<DataColumn<?>> result = new HashSet<>();
    for (Property<?> property : properties) {
      result.add(addColumn(property));
    }
    return result;
  }

  /**
   * The columns of the data table in insertion order. The subsequent calls of the method always
   * return the columns in the same order.
   * 
   * @return
   */
  public Collection<DataColumn<?>> columns() {
    return columnMap.values();
  }

  /**
   * Return all the properties contained by the TableData.
   * 
   * @return
   */
  public List<Property<?>> properties() {
    return properties(c -> true);
  }

  /**
   * Return the properties defined by the kind.
   * 
   * @param kind The kind predicate.
   * @return
   */
  public List<Property<?>> properties(Predicate<DataColumn<?>> kind) {
    return columnMap.values().stream().filter(kind).map(dc -> dc.getProperty())
        .collect(Collectors.toList());
  }

  /**
   * This add a new stored column so the property must be owned by the {@link #entityDef}. Or else
   * we might need some join information.
   * 
   * @param <T>
   * @param property Property of the {@link #entityDef}.
   * @return
   */
  public <T> DataColumn<T> addColumn(Property<T> property) {
    if (property instanceof PropertyRef<?>) {
      return addColumnRef((PropertyRef<T>) property);
    } else {
      return addColumnOwn(property);
    }
  }

  /**
   * The varargs version of add column where you can specify the Properties to add in a convenient
   * way.
   * 
   * @param properties
   * @return If the parameter is empty then returns a {@link Collections#emptyList()}.
   */
  public List<DataColumn<?>> addColumns(Property<?>... properties) {
    if (properties == null || properties.length == 0) {
      return Collections.emptyList();
    }
    List<DataColumn<?>> result = new ArrayList<>(properties.length);
    for (int i = 0; i < properties.length; i++) {
      result.add(addColumn(properties[i]));
    }
    return result;
  }

  final <T> DataColumnRef<T> addColumnRef(PropertyRef<T> property) {
    @SuppressWarnings("unchecked")
    DataColumnRef<T> dataColumn = (DataColumnRef<T>) columnMap.get(property.getName());
    if (dataColumn != null) {
      return dataColumn;
    }
    List<Reference<?, ?>> joinPath = property.getJoinReferences();
    TableData<?> current = this;
    // The reference path for the data. Must be collected or created based on the meta join
    // path.
    List<DataReference<?, ?>> referencePath = new ArrayList<>(5);
    for (Reference<?, ?> reference : joinPath) {
      // Identify if the reference exits in the current data table.
      DataReference<?, ?> dataReference = current.referenceTo.get(reference);
      if (dataReference == null) {
        // The reference is not existing up till now. We have to initiate it.
        // TODO Later on we have to resolve the data table for the entity.
        TableData<?> target = new TableData<>(reference.getTarget());
        for (Join<?> join : reference.joins()) {
          target.addColumn(join.getTargetProperty());
        }
        dataReference = current.addReferenceTypeless(reference, target);
      }
      referencePath.add(dataReference);
      current = dataReference.target();
    }
    dataColumn = new DataColumnRef<>(referencePath, property);
    // At the end we must add the column to the final data table! It's defined by the property
    // variable of the referencing property we add currently. The final data table is the
    // current. We need this newly added column for our DataColumnRef because it's the owned
    // column that belongs to this.
    dataColumn.setOwnedColumn(current.addColumnOwn(property.getReferredOwnedProperty()));
    columnMap.put(property.getName(), dataColumn);
    return dataColumn;
  }


  public <T> DataColumnOwned<T> addColumnOwn(Property<T> property) {
    @SuppressWarnings("unchecked")
    DataColumnOwned<T> dataColumn = (DataColumnOwned<T>) columnMap.get(property.getName());
    if (dataColumn != null) {
      return dataColumn;
    }
    // The new column is added to the registry of this data table. If it's an owned property then
    // a new owned column will be added.
    dataColumn = new DataColumnOwned<>(property);
    columnMap.put(property.getName(), dataColumn);
    return dataColumn;
  }

  /**
   * Adding a new reference to the data table. It's type aware so you can add only the appropriate
   * reference that matches the E - entity definition of the entity of the data table.
   * 
   * @param referenceDef
   * @param targetTable
   * @return
   */
  public <T extends EntityDefinition> DataReference<E, T> addReference(Reference<E, T> referenceDef,
      TableData<T> targetTable) {
    if (referenceDef == null || targetTable == null) {
      return null;
    }
    // This can be unchecked because the Reference is typed properly.
    @SuppressWarnings("unchecked")
    DataReference<E, T> dataReference = (DataReference<E, T>) referenceTo.get(referenceDef);
    if (dataReference == null) {
      // We create a new data reference.
      dataReference = new DataReference<>(referenceDef, this, targetTable);
      referenceTo.put(referenceDef, dataReference);
      // At the same time we have to add this reference to the targetTable as backReference.
      // targetTable.referredBy.put(key, value)
    }
    return dataReference;
  }

  /**
   * Adding a new back reference (points to this entity) to the {@link TableData}. This reference
   * must point to E (this entity) to identify the way how the detail records joined.
   * 
   * CAUTION! It hasn't been tested yet!!!
   * 
   * @param <D>
   * @param referenceDef
   * @param detailTable
   * @return
   */
  public <D extends EntityDefinition> DataReference<D, E> addBackReference(
      Reference<D, E> referenceDef, TableData<D> detailTable) {
    // This can be unchecked because the Reference is typed properly.
    @SuppressWarnings("unchecked")
    DataReference<D, E> dataReference = (DataReference<D, E>) referredBy.get(referenceDef);
    if (dataReference == null) {
      // We can add the back reference here.
      dataReference = new DataReference<>(referenceDef, detailTable, this);
    }
    return dataReference;
  }

  /**
   * Adding a new reference to the data table. It's type aware so you can add only the appropriate
   * reference that matches the E - entity definition of the entity of the data table.
   * 
   * @param reference
   * @param target
   * @return
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private DataReference<?, ?> addReferenceTypeless(Reference<?, ?> reference, TableData<?> target) {
    // This is
    DataReference dataReference = referenceTo.get(reference);
    if (dataReference == null) {
      // We create a new data reference.
      dataReference = new DataReference(reference, this, target);
      // TODO Add some initialization.
      referenceTo.put((Reference<E, ?>) reference, dataReference);
    }
    return dataReference;
  }

  /**
   * With this function we expand the data table with a new row. This new row will be added as the
   * last row of the physical list of the rows.
   * 
   * @return We get back the newly created row object. It's a direct reference for the newly created
   *         row.
   * @throws InvalidRowException
   */
  public DataRow addRow() {
    return rowModel.addRow(1, null, true).get(0);
  }

  /**
   * The {@link EntityDefinition} that defines the content of the entity data table. Used to access
   * the meta data easily.
   * 
   * @return
   */
  public final E entity() {
    return entityDef;
  }

  /**
   * We can use the list of rows for accessing the records in the data table.
   * 
   * @return An unmodifiable list of the rows.
   */
  public List<DataRow> rows() {
    return Collections.unmodifiableList(rowModel.rows);
  }

  /**
   * We can iterate through the table data row by row. Every row is returned as a bean created as
   * the interface instance passed in the clazz parameter. The instances for the rows are different
   * instances but all of them refers to the given row. If we set a value on the bean then this
   * value will be set directly into the {@link TableData}. So it's only a reference.
   * 
   * @param <T> The class of the parameter interface.
   * @param clazz
   * @return The iterable for the for each.
   */
  public <T> Iterable<T> beansByRef(final Class<T> clazz) {
    return new Iterable<T>() {

      @Override
      public Iterator<T> iterator() {
        try {
          return new Iterator<T>() {

            Iterator<DataRow> rowIterator = rows().iterator();

            TableDataBeanBinding beanMapper = getBeanBinding(clazz);

            @Override
            public boolean hasNext() {
              return rowIterator.hasNext();
            }

            @Override
            public T next() {
              @SuppressWarnings("unchecked")
              T bean = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz},
                  new DataRowRefBeanInvocationHandler(beanMapper, rowIterator.next()));
              return bean;
            }

            @Override
            public void remove() {
              // Unable to remove the record while iterating.
              throw new RuntimeException("The remove is not implemented on the bean iterator.");
            }
          };
        } catch (Exception e) {
          // Unable to construct the bean iterator.
          throw new RuntimeException(
              "Unable to construct the bean iterator for the " + clazz.getName() + " bean.", e);
        }
      }

    };
  }

  /**
   * We can iterate through the table data row by row. Every row is returned as a bean created as
   * the interface instance passed in the clazz parameter. The instances for the rows are different
   * instances and contains the copied values of the given row. If we set a value on the bean then
   * this value will be set only in the bean. So it doesn't modify the values of the
   * {@link TableData}.
   * 
   * @param <T> The class of the parameter interface.
   * @param clazz
   * @return The iterable for the for each.
   */
  public <T> Iterable<T> beansByVal(final Class<T> clazz) {
    return new Iterable<T>() {

      @Override
      public Iterator<T> iterator() {
        try {
          return new Iterator<T>() {

            Iterator<DataRow> rowIterator = rows().iterator();

            TableDataBeanBinding beanMapper =
                getBeanBinding(clazz);

            @Override
            public boolean hasNext() {
              return rowIterator.hasNext();
            }

            @Override
            public T next() {
              @SuppressWarnings("unchecked")
              T bean = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz},
                  new DataRowValBeanInvocationHandler(beanMapper, rowIterator.next()));
              return bean;
            }

            @Override
            public void remove() {
              // Unable to remove the record while iterating.
              throw new RuntimeException("The remove is not implemented on the bean iterator.");
            }
          };
        } catch (Exception e) {
          // Unable to construct the bean iterator.
          throw new RuntimeException(
              "Unable to construct the bean iterator for the " + clazz.getName() + " bean.", e);
        }
      }

    };
  }

  @Override
  public String toString() {
    return TableDatas.toString(this);
  }

  /**
   * The collection of the references to other data tables.
   * 
   * @return
   */
  public Collection<DataReference<E, ?>> referenceTo() {
    return referenceTo.values();
  }

  /**
   * The collection of the references to this data table from others.
   * 
   * @return
   */
  public Collection<DataReference<?, E>> referredBy() {
    return referredBy.values();
  }

  /**
   * Can retrieve the reference to another data table by the reference definition from the entity.
   * 
   * @param <T> The target entity is defined by the type parameter.
   * @param reference The data reference if any. Its typed by the entity of the current data table
   *        and the entity of the reference target.
   * @return The data reference for the given meta reference.
   */
  @SuppressWarnings("unchecked")
  public <T extends EntityDefinition> DataReference<E, T> referenceTo(Reference<E, T> reference) {
    return (DataReference<E, T>) referenceTo.get(reference);
  }

  /**
   * Can retrieve the reference from another data table by the reference definition of the entity.
   * 
   * @param <T> The source entity is defined by the type parameter.
   * @param reference The data reference if any. Its typed by the entity of the reference target and
   *        the entity of the current data table.
   * @return The data reference for the given meta reference.
   */
  @SuppressWarnings("unchecked")
  public <T extends EntityDefinition> DataReference<T, E> referredBy(Reference<T, E> reference) {
    return (DataReference<T, E>) referredBy.get(reference);
  }

  /**
   * Retrieve the {@link TableDataBeanBinding} object for the given bean class.
   * 
   * @param beanClass The bean class that we would like to map to the table data.
   * @return
   * @throws ExecutionException
   */
  TableDataBeanBinding getBeanBinding(Class<?> beanClass) throws Exception {
    TableDataBeanBinding result = null;
    if (beanMappers == null) {
      beanMappers = new HashMap<>();
    } else {
      result = beanMappers.get(beanClass);
    }
    if (result == null) {
      result =
          new TableDataBeanBinding(this, beanClass);
      beanMappers.put(beanClass, result);
    }
    return result;
  }

  /**
   * Construct the list of beans based on the bean class. The result is the same when we iterate
   * over the table data and get the bean for every row.
   * 
   * @param <B> The bean class.
   * @param beanClass The bean class that can be interface also but in this case the instances will
   *        be proxies with {@link DataRowValBeanInvocationHandler}. Inside the invocation handler
   *        it's a Map so be careful with the performance!
   * @return The list of beans for every row in the table data.
   * @throws Exception
   */
  public <B> List<B> asList(Class<B> beanClass) throws Exception {
    List<B> result = new ArrayList<>(size());
    TableDataBeanBinding binding = getBeanBinding(beanClass);
    for (DataRow row : rows()) {
      result.add(row.get(beanClass, binding));
    }
    return result;
  }

  /**
   * With this call we initiate the online {@link TableDataIndexSet} instance for this
   * {@link TableData}. If we create index for some of the
   * 
   * @return
   */
  public TableDataIndexSet index() {
    if (indexSet == null) {
      indexSet = new TableDataIndexSet(this);
    }
    return indexSet;
  }

  /**
   * Clear all rows in tabledata.
   * 
   */
  public void clearRows() {
    rowModel.clear();
  }
}
