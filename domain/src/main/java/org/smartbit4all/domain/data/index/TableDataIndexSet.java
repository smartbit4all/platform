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
  @SuppressWarnings("unchecked")
  public <T> UniqueIndex<T> unique(Property<T> property) {
    String indexName = TableDataIndex.constructIndexName(IndexType.UNIQUE, Arrays.asList(property));
    return (UniqueIndex<T>) indices.computeIfAbsent(indexName, (idxName) -> {
      return new UniqueIndex<T>(tableRef.get(), property);
    });
  }

  /**
   * Get or create the unique index for the given property.
   * 
   */
  @SuppressWarnings("unchecked")
  public <T1 extends Comparable<?>, T2 extends Comparable<?>> UniqueIndex2<T1, T2> unique(
      Property<T1> property1, Property<T2> property2) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.UNIQUE, Arrays.asList(property1, property2));
    return (UniqueIndex2<T1, T2>) indices.computeIfAbsent(indexName, (idxName) -> {
      return new UniqueIndex2<>(tableRef.get(), property1, property2);
    });
  }

  /**
   * Get or create the unique index for the given property.
   * 
   */
  @SuppressWarnings("unchecked")
  public <T> UniqueIndex<T> unique(DataColumn<T> column) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.UNIQUE, Arrays.asList(column.getProperty()));
    return (UniqueIndex<T>) indices.computeIfAbsent(indexName, (idxName) -> {
      return new UniqueIndex<>(tableRef.get(), column);
    });
  }

  /**
   * Get or create the unique index for the given property.
   * 
   */
  @SuppressWarnings("unchecked")
  public <T1 extends Comparable<?>, T2 extends Comparable<?>> UniqueIndex2<T1, T2> unique(
      DataColumn<T1> column1, DataColumn<T2> column2) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.UNIQUE,
            Arrays.asList(column1.getProperty(), column2.getProperty()));
    return (UniqueIndex2<T1, T2>) indices.computeIfAbsent(indexName, (idxName) -> {
      return new UniqueIndex2<>(tableRef.get(), column1, column2);
    });
  }

  /**
   * Get or create the index for the given property.
   * 
   * @param property The property.
   * 
   */
  @SuppressWarnings("unchecked")
  public <T> NonUniqueIndex<T> nonUnique(Property<T> property) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.NONUNIQUE, Arrays.asList(property));
    return (NonUniqueIndex<T>) indices.computeIfAbsent(indexName, (idxName) -> {
      return new NonUniqueIndex<>(tableRef.get(), property);
    });
  }

  /**
   * Get or create the index for the given property.
   * 
   */
  @SuppressWarnings("unchecked")
  public <T1 extends Comparable<?>, T2 extends Comparable<?>> NonUniqueIndex2<T1, T2> nonUnique(
      Property<T1> property1, Property<T2> property2) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.NONUNIQUE, Arrays.asList(property1, property2));
    return (NonUniqueIndex2<T1, T2>) indices.computeIfAbsent(indexName, (idxName) -> {
      return new NonUniqueIndex2<>(tableRef.get(), property1, property2);
    });
  }

  /**
   * Get or create the index for the given property.
   * 
   */
  @SuppressWarnings("unchecked")
  public <T> NonUniqueIndex<T> nonUnique(DataColumn<T> column) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.NONUNIQUE, Arrays.asList(column.getProperty()));
    return (NonUniqueIndex<T>) indices.computeIfAbsent(indexName, (idxName) -> {
      return new NonUniqueIndex<>(tableRef.get(), column);
    });
  }

  /**
   * Get or create the index for the given property.
   * 
   */
  @SuppressWarnings("unchecked")
  public <T1 extends Comparable<?>, T2 extends Comparable<?>> NonUniqueIndex2<T1, T2> nonUnique(
      DataColumn<T1> column1, DataColumn<T2> column2) {
    String indexName =
        TableDataIndex.constructIndexName(IndexType.NONUNIQUE,
            Arrays.asList(column1.getProperty(), column2.getProperty()));
    return (NonUniqueIndex2<T1, T2>) indices.computeIfAbsent(indexName, (idxName) -> {
      return new NonUniqueIndex2<>(tableRef.get(), column1, column2);
    });
  }

  /**
   * Returns the index for the given column if any.
   * 
   * @return The index if exists and null if we don't have any.
   */
  public TableDataIndex find(IndexType type, Property<?>... properties) {
    String indexName =
        TableDataIndex.constructIndexName(type, Arrays.asList(properties));
    return indices.get(indexName);
  }

}
