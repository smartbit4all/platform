package org.smartbit4all.domain.service.query;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.ExpressionExists;
import org.smartbit4all.domain.meta.ExpressionInDataSet;
import org.smartbit4all.domain.meta.ExpressionReplacer;
import org.smartbit4all.domain.meta.OperandProperty;
import org.smartbit4all.domain.service.dataset.DataSetApi;
import org.smartbit4all.domain.service.dataset.DataSetEntry;

public final class QueryAndSaveResultAsDataSet extends SB4FunctionImpl<Query<?>, DataSetEntry> {

  /**
   * {@link DataSetApi} to save the result of the query.
   */
  private DataSetApi dataSetApi;

  /**
   * The full expression where the exists must be replaced.
   */
  private Expression fullExpression;

  /**
   * The original exists that must be replaced if the query and the save of the data set have been
   * succeeded.
   */
  private ExpressionExists originalExists;

  public QueryAndSaveResultAsDataSet(DataSetApi dataSetApi, Expression fullExpression,
      ExpressionExists originalExists) {
    super();
    this.dataSetApi = dataSetApi;
    this.fullExpression = fullExpression;
    this.originalExists = originalExists;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void execute() throws Exception {
    if (dataSetApi != null) {
      // First we execute the query.
      TableData<?> data = input().listData();
      // If the result is empty then we can add a false boolean expression instead of the original
      // exists.
      if (data == null || data.isEmpty()) {
        ExpressionReplacer.replace(fullExpression, originalExists,
            Expression.FALSE());
        return;
      }
      // We save the data set from the first column of the query result and replace the expression
      // with an exists.
      Iterator<DataColumn<?>> iterColumn = data.columns().iterator();
      DataSetEntry entry = null;
      if (iterColumn.hasNext()) {
        DataColumn<?> column = iterColumn.next();
        List<?> values = data.values(column);
        HashSet valueSet = new HashSet(values);
        // The empty value (null) is not a valid reference. So we can skip this.
        valueSet.remove(null);
        // If we doesn't have any more result then we can skip the result set and use a FALSE
        // instead of the original expression.
        if (valueSet.isEmpty()) {
          ExpressionReplacer.replace(fullExpression, originalExists,
              Expression.FALSE());
          return;
        }
        entry = dataSetApi
            .activate(column.getProperty(), valueSet);
      }
      if (entry != null) {
        ExpressionReplacer.replace(fullExpression, originalExists,
            new ExpressionInDataSet(new OperandProperty(originalExists.getContextProperty()),
                entry));
      }
    }
  }

}
