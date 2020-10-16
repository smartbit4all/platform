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
