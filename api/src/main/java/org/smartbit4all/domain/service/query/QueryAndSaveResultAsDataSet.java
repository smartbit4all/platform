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
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.service.CrudApis;
import org.smartbit4all.domain.service.dataset.DataSetApi;
import org.smartbit4all.domain.service.dataset.DataSetEntry;

public final class QueryAndSaveResultAsDataSet extends SB4FunctionImpl<QueryInput, DataSetEntry> {

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

  private boolean inMemoryMaster = false;

  public QueryAndSaveResultAsDataSet(DataSetApi dataSetApi, Expression fullExpression,
      ExpressionExists originalExists, boolean inMemoryMaster) {
    super();
    this.dataSetApi = dataSetApi;
    this.fullExpression = fullExpression;
    this.originalExists = originalExists;
    this.inMemoryMaster = inMemoryMaster;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void execute() throws Exception {
    // First we execute the query.

    System.out.println(input());

    QueryOutput queryOutput = CrudApis.getCrudApi().executeQuery(input());
    if (queryOutput.isResultSerialized()) {
      throw new IllegalStateException(
          "QueryOutput can not contain serialized result for saving it as a DataSet!");
    }
    TableData<?> data = queryOutput.getTableData();

    // If the result is empty then we can add a false boolean expression instead of the original
    // exists.
    if (data == null || data.isEmpty()) {
      if (originalExists.isNegate()) {
        ExpressionReplacer.replace(fullExpression, originalExists,
            Expression.TRUE());
      } else {
        ExpressionReplacer.replace(fullExpression, originalExists,
            Expression.FALSE());
      }
      return;
    }
    // We save the data set from the first column of the query result and replace the expression
    // with an exists.
    Iterator<DataColumn<?>> iterColumn = data.columns().iterator();
    DataSetEntry entry = null;
    HashSet valueSet = new HashSet();
    if (iterColumn.hasNext()) {
      DataColumn<?> column = iterColumn.next();
      List<?> values = data.values(column);
      valueSet.addAll(values);
      // The empty value (null) is not a valid reference. So we can skip this.
      valueSet.remove(null);
      // If we doesn't have any more result then we can skip the result set and use a FALSE
      // instead of the original expression.
      if (valueSet.isEmpty()) {
        ExpressionReplacer.replace(fullExpression, originalExists,
            Expression.FALSE());
        return;
      }
      if (dataSetApi != null && !inMemoryMaster) {
        entry = dataSetApi
            .activate(column.getProperty(), valueSet);
        // The dataSetApi is available and could save and activate the data set into the database.
        ExpressionInDataSet expressionInDataSet =
            new ExpressionInDataSet(new OperandProperty(originalExists.getContextProperty()),
                entry);
        if (originalExists.isNegate()) {
          expressionInDataSet.NOT();
        }

        ExpressionReplacer.replace(fullExpression, originalExists, expressionInDataSet);
      } else if (!valueSet.isEmpty()) {
        Property<?> targetProperty = originalExists
            .getMasterReferencePath().last().joins().get(0).getTargetProperty();
        ExpressionReplacer.replace(fullExpression, originalExists,
            originalExists.isNegate() ? targetProperty.notin(valueSet)
                : targetProperty.in(valueSet));
      }
    }
  }

}
