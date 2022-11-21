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
package org.smartbit4all.domain.data.filtering;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.index.UniqueIndex;

/**
 * This step is responsible for evaluating the equal, not equal, in, not in expressions
 * 
 * @author Zoltán Suller
 *
 */
public class EvaluationIndexedUnique<T> extends EvaluationStep {

  /**
   * If this is true, then at the end the result will be collected from the non matching rows.
   */
  private final boolean invert;

  /**
   * This is the index for the values. It contains a matching row for every single value. The index
   * is not sorted so it can be used only for the search and not navigate.
   */
  private final UniqueIndex<T> idx;

  /**
   * The values contains all the values that must be checked against the index.
   */
  private final List<T> valuesToSearch;

  EvaluationIndexedUnique(UniqueIndex<T> index, List<T> values, boolean invert) {
    super();
    this.idx = index;
    this.valuesToSearch = values;
    this.invert = invert;
  }

  @Override
  List<DataRow> execute(TableData<?> tableData, List<DataRow> rows) {
    List<DataRow> matchingRows = new ArrayList<>();
    for (T t : valuesToSearch) {
      matchingRows.add(idx.get(t));
    }
    matchingRows = DataRow.sortByPosition(matchingRows);

    return invert ? DataRow.minus(rows, matchingRows) : DataRow.intersect(rows, matchingRows);
  }

  @Override
  protected void print(StringBuilder buffer, String prefix, String childrenPrefix) {
    buffer.append(prefix);
    buffer.append("EvaluationIndexedUnique");
    buffer.append('\n');
  }

}
