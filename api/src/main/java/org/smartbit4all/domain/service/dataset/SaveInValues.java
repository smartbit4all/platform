package org.smartbit4all.domain.service.dataset;

import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.ExpressionIn;
import org.smartbit4all.domain.meta.ExpressionInDataSet;
import org.smartbit4all.domain.meta.ExpressionReplacer;
import org.smartbit4all.domain.meta.OperandProperty;

public final class SaveInValues extends SB4FunctionImpl<ExpressionIn<?>, DataSetEntry> {

  /**
   * The full expression where the exists must be replaced.
   */
  private Expression fullExpression;

  /**
   * {@link DataSetApi} to save the result of the query.
   */
  private DataSetApi dataSetApi;

  public SaveInValues(DataSetApi dataSetApi, Expression fullExpression,
      ExpressionIn<?> inExpression) {
    setInput(inExpression);
    this.setFullExpression(fullExpression);
    this.dataSetApi = dataSetApi;
  }

  @Override
  public void execute() throws Exception {
    if (dataSetApi != null) {
      // We save the data set and replace the expression with an exists.
      DataSetEntry entry = dataSetApi
          .activate(((OperandProperty<?>) input().getOperand()).property(), input.values());
      if (entry != null) {
        ExpressionInDataSet expressionInDataSet =
            new ExpressionInDataSet(input().getOperand(), entry);
        expressionInDataSet.setNegate(input().isNegate());
        ExpressionReplacer.replace(fullExpression, input(),
            expressionInDataSet);
      }
    }
  }

  public final Expression getFullExpression() {
    return fullExpression;
  }

  public final void setFullExpression(Expression fullExpression) {
    this.fullExpression = fullExpression;
  }

}
