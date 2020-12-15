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
package org.smartbit4all.domain.data;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.smartbit4all.domain.data.TableDataBeanBinding.DataBind;
import org.smartbit4all.domain.meta.DataConverter;
import org.smartbit4all.domain.meta.Property;

/**
 * The {@link TableData} consists of column array. Every column has a list with the values. The row
 * itself is nothing else but the index of the given record in the value array. In a data table
 * there is an instance for every row. This indirection helps to avoid the problems of the direct
 * indices confusion. The row objects are strictly contained by the given data table. At the same
 * time the record must be identified by some business information. This value is referred by this
 * record. So every record can be identified exactly by this. This is the basic mechanism for
 * connecting the related records.
 * 
 * @author Peter Boros
 *
 */
public final class DataRow {

  /**
   * Compare the two row by their position in ascending order.
   */
  static final Comparator<DataRow> ComparatorByPositionAsc = new Comparator<DataRow>() {

    @Override
    public int compare(DataRow o1, DataRow o2) {
      // In case of null we return equals but it doesn't matter.
      if (o1 == null || o2 == null) {
        return 0;
      }
      return o1.getPosition() - o2.getPosition();
    }
  };

  /**
   * Compare the two row by their position in descending order.
   */
  static final Comparator<DataRow> ComparatorByPositionDesc = new Comparator<DataRow>() {

    @Override
    public int compare(DataRow o1, DataRow o2) {
      // In case of null we return equals but it doesn't matter.
      if (o1 == null || o2 == null) {
        return 0;
      }
      return o2.getPosition() - o1.getPosition();
    }
  };

  static final int I_INVALIDINDEX = -1;

  /**
   * TableData, which contains this DataRow.
   * 
   * TODO weakref?
   * 
   */
  private TableData<?> tableData;

  /**
   * Refer to a valid row or -1 if the row is not existing any more. In this case the given row is
   * not usable any more. This is the index of the data row in the columns data array and in the
   * referenced rows in the {@link DataReference}.
   */
  private int rowDataIndex;

  /**
   * This is the index of the given row in the {@link TableDataRowModel}. It's important to know
   * because it will help us to identify the position in the row list. To keep the consistent data
   * table the position of the currently valid rows must be indexed from 0 to size()-1. If it's out
   * of this range then the given row is currently invalid.
   */
  private int position;

  /**
   * Creates a new data row at the given position with the data index.
   * 
   * @param position
   * @param rowDataIndex
   */
  DataRow(TableData<?> tableData, int position, int rowDataIndex) {
    super();
    this.tableData = tableData;
    this.position = position;
    this.rowDataIndex = rowDataIndex;
  }

  /**
   * The row is invalid if the {@link #rowDataIndex} doesn't point to a valid row. If it's negative
   * then it's obvious.
   * 
   * @return
   */
  public final boolean isValid() {
    return position >= 0;
  }

  /**
   * Preset the index of the data and the position of the row.
   */
  final void invalidate() {
    position = I_INVALIDINDEX;
  }

  /**
   * Refer to a valid row or -1 if the row is not existing any more. In this case the given row is
   * not usable any more. This is the index of the data row in the columns data array and in the
   * referenced rows in the {@link DataReference}.
   * 
   * @see #rowDataIndex
   * @return
   */
  final int getRowDataIndex() {
    return rowDataIndex;
  }

  /**
   * @see #rowDataIndex
   * @param rowDataIndex
   */
  final void setRowDataIndex(int rowDataIndex) {
    this.rowDataIndex = rowDataIndex;
  }

  /**
   * @see #position
   * @return
   */
  final int getPosition() {
    return position;
  }

  /**
   * @see #position
   * @param position
   */
  final void setPosition(int position) {
    this.position = position;
  }

  /**
   * Modify the position value with the delta.
   * 
   * @param delta
   */
  final void modifyPosition(int delta) {
    position += delta;
  }

  public static final List<DataRow> sortByPosition(List<DataRow> rows) {
    return rows.stream().sorted(Comparator.comparingInt(DataRow::getPosition))
        .collect(Collectors.toList());
  }

  /**
   * The intersect is a basic set operation that results a list of rows contained by both list. We
   * used the following published pseudo code for the implementation.
   * 
   * @see <a href="https://www.geeksforgeeks.org/union-and-intersection-of-two-linked-lists/">Union
   *      and intersect of two sorted lists</a>
   * 
   * 
   * @param rows1 List of rows. The list must be sorted by the position of the rows.
   * @param rows2 List of rows. The list must be sorted by the position of the rows.
   * @return
   */
  public static final List<DataRow> intersect(List<DataRow> rows1, List<DataRow> rows2) {
    if (rows1 == null) {
      rows1 = Collections.emptyList();
    }
    if (rows2 == null) {
      rows2 = Collections.emptyList();
    }
    // If any of the lists is empty then the result is empty
    if (rows1.isEmpty() || rows2.isEmpty()) {
      return Collections.emptyList();
    }
    List<DataRow> result = new ArrayList<>(Math.min(rows1.size(), rows2.size()));
    int j = 0;
    int i = 0;
    // The loop is iterating till the end of the lists. If any of them is at the end then we stop
    // iterating.
    while (i < rows1.size() && j < rows2.size()) {
      // If they are equal then add it to the result. The rows as objects are identical in a data
      // table. So we can compare then with ==.
      if (rows1.get(i) == rows2.get(j)) {
        result.add(rows1.get(i));
        i++;
        j++;
      } else {
        // We step forward with list that has the smaller indexed row.
        if (rows1.get(i).getPosition() < rows2.get(j).getPosition()) {
          i++;
        } else {
          j++;
        }
      }
    }
    return result;
  }

  /**
   * This is a set operation where we remove all the rows from the basic list that is contained by
   * the minus list. The algorithm is published at:
   * 
   * @see <a href="https://www.geeksforgeeks.org/union-and-intersection-of-two-linked-lists/">Union
   *      and intersect of two sorted lists</a>
   * @param basicList
   * @param minus
   * @return
   */
  public static final List<DataRow> minus(List<DataRow> basicList, List<DataRow> minus) {
    // If the basic list is already empty then the result must be empty without knowing
    // anything about the minus list.
    if (basicList == null || basicList.isEmpty()) {
      return Collections.emptyList();
    }
    // If the minus list is empty then there is nothing to minus. So the result will the original
    // basic list.
    if (minus == null || minus.isEmpty()) {
      return basicList;
    }
    // Here we can be sure that both lists contains elements. We estimate the size of the result to
    // reduce memory fragmentation.
    List<DataRow> result = new ArrayList<>(basicList.size() - minus.size());
    int j = 0;
    int i = 0;
    // We iterate until we have new row from the basic list. There is no need to go further because
    // there will be no more row to keep.
    while (i < basicList.size()) {
      // If we hasn't reach the end of the minus list and we have the same row then we skip it and
      // go further with both list.
      if (minus.size() != j && basicList.get(i) == minus.get(j)) {
        i++;
        j++;
      } else {
        if (j == minus.size() || basicList.get(i).getPosition() < minus.get(j).getPosition()) {
          // If we reached the end of the minus or the basic list row is smaller than the minus list
          // one we add the basic row to the result and go further with the basic list.
          result.add(basicList.get(i));
          i++;
        } else {
          // Else we can go further with the minus list.
          j++;
        }
      }
    }
    return result;
  }

  /**
   * Union of the row lists. They must be sorted.
   * 
   * @param rowLists
   * @return The result contains every row only once. So it's a distinct, sorted list of all the
   *         rows from any of these lists.
   */
  public static final List<DataRow> union(List<List<DataRow>> rowLists) {
    if (rowLists == null || rowLists.isEmpty()) {
      return Collections.emptyList();
    }
    int idx[] = new int[rowLists.size()];
    // Initiate at the beginning index.
    Arrays.fill(idx, 0);
    int sizes[] = new int[rowLists.size()];
    int maxSize = 0;
    // Initiate the sizes of the row lists.
    for (int i = 0; i < sizes.length; i++) {
      sizes[i] = rowLists.get(i).size();
      if (maxSize < sizes[i]) {
        maxSize = sizes[i];
      }
    }
    // We will iterate until we reach the end of all row list.
    List<DataRow> result = new ArrayList<>(maxSize);
    DataRow nextRow;
    while ((nextRow = getNextRow(rowLists, idx)) != null) {
      // This row will be added to the result
      result.add(nextRow);
    }
    return result;
  }

  /**
   * Find out which one is the next and step forward at the same time.
   * 
   * @param rowLists The row lists that are under merge.
   * @param index The index array with the current positions.
   * @return The next
   */
  private static final DataRow getNextRow(List<List<DataRow>> rowLists, int index[]) {
    DataRow nextRow = null;
    int listToStep = -1;
    for (int i = 0; i < index.length; i++) {
      List<DataRow> rowList = rowLists.get(i);
      if (index[i] < rowList.size()) {
        DataRow row = rowList.get(index[i]);
        if (nextRow == null || row.getPosition() < nextRow.getPosition()) {
          nextRow = row;
          listToStep = i;
        }
      }
    }
    if (nextRow != null) {
      index[listToStep]++;
    }
    return nextRow;
  }

  /**
   * see {@link TableData#get(DataColumn, DataRow)}
   * 
   * @param <T>
   * @param column
   * @return
   */
  public <T> T get(Property<T> column) {
    DataColumn<T> dataColumn = tableData.getColumn(column);
    if(dataColumn == null) {
      throw new RuntimeException("There is no column in the referred TableData with the given Property: " + column.getName());
    }
    return tableData.get(dataColumn, this);
  }

  /**
   * see {@link TableData#get(DataColumn, DataRow, DataConverter)}
   * 
   * @param <C>
   * @param <T>
   * @param column
   * @param row
   * @param converter
   * @return
   */
  public <C, T> C get(Property<T> column,
      DataConverter<T, C> converter) {
    return tableData.get(tableData.getColumn(column), this, converter);
  }

  /**
   * We can retrieve the property values named by the parameter in a {@link Map}.
   * 
   * @param properties The properties we need. If it's missing then we get back all the values.
   * @return The {@link Map} with the Properties as keys and the values. The null values will be
   *         skipped.
   */
  public Map<Property<?>, Object> values(Property<?>... properties) {
    if (tableData.columns().isEmpty()) {
      return Collections.emptyMap();
    }
    Map<Property<?>, Object> result = new HashMap<>();
    if (properties == null || properties.length == 0) {
      for (DataColumn<?> column : tableData.columns()) {
        result.put(column.getProperty(), tableData.get(column, this));
      }
    } else {
      for (int i = 0; i < properties.length; i++) {
        result.put(properties[i], get(properties[i]));
      }
    }
    return result;
  }

  /**
   * see {@link TableData#set(DataColumn, DataRow, Object)}
   * 
   * @param <T>
   * @param column
   * @param value
   */
  public <T> void set(Property<T> column, T value) {
    tableData.set(tableData.getColumn(column), this, value);
  }

  /**
   * see {@link TableData#set(DataColumn, DataRow, Object)}
   * 
   * @param <T>
   * @param column
   * @param value
   */
  public <T> void set(DataColumn<T> column, T value) {
    tableData.set(column, this, value);
  }

  /**
   * see {@link TableData#set(DataColumn, DataRow, Object)}
   * 
   * @param <T>
   * @param column
   * @param value
   */
  public void setObject(DataColumn<?> column, Object value) {
    tableData.setObject(column, this, value);
  }

  /**
   * see {@link TableData}#
   * 
   * @param column
   * @param value
   */
  public void setObject(Property<?> column, Object value) {
    tableData.setObject(tableData.getColumn(column), this, value);
  }

  /**
   * see {@link TableData#set(DataColumn, DataRow, Comparable)}
   * 
   * @param <T>
   * @param column
   * @param value
   * @throws ExecutionException If something went wrong with the mapping of the given bean then we
   *         have to handle the exception.
   */
  public <T> void set(T bean) throws Exception {
    if (bean == null) {
      // Skip the set.
      return;
    }
    TableDataBeanBinding binding = tableData.getBeanBinding(bean.getClass());
    for (Entry<Method, DataBind> entry : binding.bindMap.entrySet()) {
      if (entry.getValue().getter) {
        DataColumn<?> column = entry.getValue().column;
        Object value;
        if (entry.getValue().converter == null) {
          value = entry.getKey().invoke(bean);
        } else {
          value = entry.getValue().converter.toward().apply(entry.getKey().invoke(bean));
        }

        setObject(column, value);
      }
    }
  }

  /**
   * With this function we can get a new instance of the parameter bean filled with the values from
   * the given row.
   * 
   * @param <T>
   * @param beanClass The bean class that we would like to initiate.
   * @return The bean instance.
   * @throws Exception
   */
  public <T> T get(Class<T> beanClass) throws Exception {
    if (beanClass == null) {
      return null;
    }

    // We try to get the mapper to be sure that we will be able to copy the values from the record
    // into the new instance of the bean.
    TableDataBeanBinding binding = tableData.getBeanBinding(beanClass);
    // The bean must have default constructor.
    return get(beanClass, binding);
  }

  /**
   * The get for optimized creation of beans without getting the binding again and again.
   * 
   * @param <T>
   * @param beanClass
   * @param binding The binding for the bean class.
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  final <T> T get(Class<T> beanClass, TableDataBeanBinding binding)
      throws Exception {
    T result;
    if (beanClass.isInterface()) {
      result = (T) Proxy.newProxyInstance(beanClass.getClassLoader(), new Class[] {beanClass},
          new DataRowValBeanInvocationHandler(binding, this));
    } else {
      result = beanClass.newInstance();
      for (Entry<Method, DataBind> entry : binding.bindMap.entrySet()) {
        if (!entry.getValue().getter) {
          DataColumn<?> column = entry.getValue().column;
          Object value;
          if (entry.getValue().converter == null) {
            value = tableData.get(column, this);
          } else {
            value = entry.getValue().converter.backward().apply(tableData.get(column, this));
          }
          entry.getKey().invoke(result, value);
        }
      }
    }
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof DataRow)) {
      return false;
    }

    DataRow oRow = (DataRow) o;

    Collection<DataColumn<?>> otherColumns = oRow.tableData.columns();
    Collection<DataColumn<?>> thisColumns = this.tableData.columns();
    if (otherColumns.size() != thisColumns.size()) {
      return false;
    }
    Map<String, Property<?>> thisPropsByName = thisColumns.stream()
        .collect(Collectors.toMap(col -> col.getProperty().getName(), col -> col.getProperty()));
    Map<String, Property<?>> otherPropsByName = otherColumns.stream()
        .collect(Collectors.toMap(col -> col.getProperty().getName(), col -> col.getProperty()));
    if (!thisPropsByName.keySet().equals(otherPropsByName.keySet())) {
      return false;
    }

    for (String propName : thisPropsByName.keySet()) {
      if (!this.get(thisPropsByName.get(propName))
          .equals(oRow.get(otherPropsByName.get(propName)))) {
        return false;
      }
    }


    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;

    for (DataColumn<?> col : this.tableData.columns()) {
      Object value = this.get(col.getProperty());
      hash = 53 * hash + (value != null ? value.hashCode() : 0);
    }
    return hash;
  }

}
