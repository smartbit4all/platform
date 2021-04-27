package org.smartbit4all.domain.service.query;

import java.util.HashSet;
import java.util.Iterator;
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
      // We save the data set from the first column of the query result and replace the expression
      // with an exists.
      Iterator<DataColumn<?>> iterColumn = data.columns().iterator();
      @SuppressWarnings("unchecked")
      DataSetEntry entry = null;
      if (iterColumn.hasNext()) {
        DataColumn<?> column = iterColumn.next();
        entry = dataSetApi
            .activate(column.getProperty(), new HashSet(data.values(column)));
      }
      if (entry != null) {
        ExpressionReplacer.replace(fullExpression, originalExists,
            new ExpressionInDataSet(new OperandProperty(originalExists.getContextProperty()),
                entry));
      }
    }
  }

}
