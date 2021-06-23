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
package org.smartbit4all.domain.data.filtering;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.index.StorageLoader;
import org.smartbit4all.domain.data.index.StorageNonUniqueIndex;

/**
 * This step is responsible for evaluating the equal, not equal, in, not in expressions by a non
 * unique storage index.
 * 
 * @author Peter Boros
 *
 */
public class EvaluationIndexedNonUniqueStorage<V> extends EvaluationStep {

  /**
   * If this is true, then at the end the result will be collected from the non matching rows.
   */
  private final boolean invert;

  /**
   * This is the index for the values. It contains a list of matching rows for every single value.
   * The index is not sorted so it can be used only for the search and not navigate.
   */
  private final StorageNonUniqueIndex<V, ?> idx;

  /**
   * The values contains all the values that must be checked against the index.
   */
  private final List<V> valuesToSearch;

  private final StorageLoader loader;

  EvaluationIndexedNonUniqueStorage(StorageLoader loader, StorageNonUniqueIndex<V, ?> index,
      List<V> values, boolean invert) {
    super();
    this.loader = loader;
    this.idx = index;
    this.valuesToSearch = values;
    this.invert = invert;
  }

  @Override
  List<DataRow> execute(TableData<?> tableData, List<DataRow> rows) {
    // If the base rows already exists and empty,
    // no need to calculate the next step, the result is going to be empty
    if(rows != null && rows.isEmpty()) {
      return Collections.emptyList();
    }
    
    Set<Object> resultValues = new HashSet<>();
    for (V v : valuesToSearch) {
      resultValues.addAll(idx.get(v));
    }

    // Append the resultValues to the TableData by the loader.
    List<DataRow> matchingRows = loader.appendKeyValues(tableData, resultValues);

    matchingRows = DataRow.sortByPosition(matchingRows);

    // If the base rows are NULL or empty,
    // no need to match against the current result.
    if(rows == null || rows.isEmpty()) {
      return matchingRows;
    }

    rows = DataRow.sortByPosition(rows);
    
    return invert ? DataRow.minus(rows, matchingRows) : DataRow.intersect(rows, matchingRows);
  }

  @Override
  protected void print(StringBuilder buffer, String prefix, String childrenPrefix) {
    buffer.append(prefix);
    buffer.append("EvaluationIndexedNonUniqueStorage");
    buffer.append('\n');
  }

}
