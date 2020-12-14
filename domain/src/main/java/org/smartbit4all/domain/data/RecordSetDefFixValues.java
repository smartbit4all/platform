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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.smartbit4all.domain.data.filtering.ExpressionEvaluation;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.ExpressionBoolean;

public class RecordSetDefFixValues<E extends EntityDefinition> extends RecordSetDefinition<E> {

  Map<DataColumn<?>, Object> valuesByColumn = new HashMap<>();

  /**
   * By default all the rows are included.
   */
  Expression expression = new ExpressionBoolean(true);

  RecordSetDefFixValues() {
    super();
  }

  <T> void add(DataColumn<T> column, T value) {
    valuesByColumn.put(column, value);
    expression = expression.AND(column.getProperty().eq(value));
  }

  @Override
  List<DataRow> filter(TableData<E> tableData, List<DataRow> rows) {
    return (new ExpressionEvaluation(tableData, null, expression)).plan().execute(rows);
  }

  @Override
  void set(TableData<E> tableData, List<DataRow> rows) {
    for (DataRow row : rows) {
      for (Entry<DataColumn<?>, Object> valueEntry : valuesByColumn.entrySet()) {
        tableData.setObject(valueEntry.getKey(), row, valueEntry.getValue());
      }
    }
  }

}
