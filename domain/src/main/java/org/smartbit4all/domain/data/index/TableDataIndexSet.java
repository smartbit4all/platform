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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.index.TableDataIndex.IndexType;
import org.smartbit4all.domain.meta.Property;

/**
 * This is an index set for a given data table. If we setup this index set on our own then it's
 * offline. It means that the index will be create once and won't be updated by modifying the
 * {@link TableData}.
 * 
 * If we use it by initiating on the {@link TableData} itself then this is an online index set that
 * is constantly updated by the modification of the data.
 * 
 * @author Peter Boros
 *
 */
public class TableDataIndexSet {

  private final WeakReference<TableData<?>> tableRef;

  /**
   * The indices in the set by the {@link Property}s as key.
   */
  private Map<String, TableDataIndex> indices = new HashMap<>();

  public TableDataIndexSet(TableData<?> table) {
    super();
    this.tableRef = new WeakReference<>(table);
  }

  /**
   * Get or create the unique index for the given property.
   * 
   * @param property The property.
   * 
   */
  public <T> UniqueIndex<T> unique(Property<T> property) {
    String indexName = TableDataIndex.constructIndexName(IndexType.UNIQUE, Arrays.asList(property));
    @SuppressWarnings("unchecked")
    UniqueIndex<T> result =
        (UniqueIndex<T>) indices.get(indexName);
    if (result == null) {
      result = new UniqueIndex<T>(tableRef.get(), property);
      indices.put(indexName, result);
    }
    return result;
  }

  /**
   * Get or create the unique index for the given property.
   * 
   * @param property The property.
   * 
   */
  public <T1 extends Comparable<?>, T2 extends Comparable<?>> UniqueIndex2<T1, T2> unique(
      Property<T1> property1, Property<T2> property2) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.UNIQUE, Arrays.asList(property1, property2));
    @SuppressWarnings("unchecked")
    UniqueIndex2<T1, T2> result =
        (UniqueIndex2<T1, T2>) indices.get(indexName);
    if (result == null) {
      result = new UniqueIndex2<>(tableRef.get(), property1, property2);
      indices.put(indexName, result);
    }
    return result;
  }

  /**
   * Get or create the unique index for the given property.
   * 
   * @param property The property.
   * 
   */
  public <T> UniqueIndex<T> unique(DataColumn<T> column) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.UNIQUE, Arrays.asList(column.getProperty()));
    @SuppressWarnings("unchecked")
    UniqueIndex<T> result =
        (UniqueIndex<T>) indices.get(indexName);
    if (result == null) {
      result = new UniqueIndex<T>(tableRef.get(), column);
      indices.put(indexName, result);
    }
    return result;
  }

  /**
   * Get or create the unique index for the given property.
   * 
   * @param property The property.
   * 
   */
  public <T1 extends Comparable<?>, T2 extends Comparable<?>> UniqueIndex2<T1, T2> unique(
      DataColumn<T1> column1, DataColumn<T2> column2) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.UNIQUE,
            Arrays.asList(column1.getProperty(), column2.getProperty()));
    @SuppressWarnings("unchecked")
    UniqueIndex2<T1, T2> result =
        (UniqueIndex2<T1, T2>) indices.get(indexName);
    if (result == null) {
      result = new UniqueIndex2<>(tableRef.get(), column1, column2);
      indices.put(indexName, result);
    }
    return result;
  }

  /**
   * Get or create the index for the given property.
   * 
   * @param property The property.
   * 
   */
  public <T> NonUniqueIndex<T> nonUnique(Property<T> property) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.NONUNIQUE, Arrays.asList(property));
    @SuppressWarnings("unchecked")
    NonUniqueIndex<T> result =
        (NonUniqueIndex<T>) indices.get(indexName);
    if (result == null) {
      result = new NonUniqueIndex<T>(tableRef.get(), property);
      indices.put(indexName, result);
    }
    return result;
  }

  /**
   * Get or create the index for the given property.
   * 
   * @param property The property.
   * 
   */
  public <T1 extends Comparable<?>, T2 extends Comparable<?>> NonUniqueIndex2<T1, T2> nonUnique(
      Property<T1> property1, Property<T2> property2) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.NONUNIQUE, Arrays.asList(property1, property2));
    @SuppressWarnings("unchecked")
    NonUniqueIndex2<T1, T2> result =
        (NonUniqueIndex2<T1, T2>) indices.get(indexName);
    if (result == null) {
      result = new NonUniqueIndex2<>(tableRef.get(), property1, property2);
      indices.put(indexName, result);
    }
    return result;
  }

  /**
   * Get or create the index for the given property.
   * 
   * @param property The property.
   * 
   */
  public <T> NonUniqueIndex<T> nonUnique(DataColumn<T> column) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.NONUNIQUE, Arrays.asList(column.getProperty()));
    @SuppressWarnings("unchecked")
    NonUniqueIndex<T> result =
        (NonUniqueIndex<T>) indices.get(indexName);
    if (result == null) {
      result = new NonUniqueIndex<T>(tableRef.get(), column);
      indices.put(indexName, result);
    }
    return result;
  }

  /**
   * Get or create the index for the given property.
   * 
   * @param property The property.
   * 
   */
  public <T1 extends Comparable<?>, T2 extends Comparable<?>> NonUniqueIndex2<T1, T2> nonUnique(
      DataColumn<T1> column1, DataColumn<T2> column2) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.NONUNIQUE,
            Arrays.asList(column1.getProperty(), column2.getProperty()));
    @SuppressWarnings("unchecked")
    NonUniqueIndex2<T1, T2> result =
        (NonUniqueIndex2<T1, T2>) indices.get(indexName);
    if (result == null) {
      result = new NonUniqueIndex2<>(tableRef.get(), column1, column2);
      indices.put(indexName, result);
    }
    return result;
  }

  /**
   * Returns the index for the given column if any.
   * 
   * @param <T>
   * @param column The column.
   * @return The index if exists and null if we don't have any.
   */
  @SuppressWarnings("unchecked")
  public TableDataIndex find(IndexType type, Property<?>... properties) {
    String indexName =
        TableDataIndex.constructIndexName(type, Arrays.asList(properties));
    return indices.get(indexName);
  }

}
