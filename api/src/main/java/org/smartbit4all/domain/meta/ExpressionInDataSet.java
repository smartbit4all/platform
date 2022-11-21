package org.smartbit4all.domain.meta;

import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.service.dataset.DataSetEntry;

public class ExpressionInDataSet extends Expression {

  private DataSetEntry dataSetEntry;

  /**
   * The operand that is checked against the temporary stored data set.
   */
  private Operand<?> operand;

  public ExpressionInDataSet(Operand<?> operand, DataSetEntry dataSetEntry) {
    super();
    this.operand = operand;
    this.dataSetEntry = dataSetEntry;
  }

  @Override
  public Expression NOT() {
    setNegate(!isNegate());
    return this;
  }

  @Override
  public void accept(ExpressionVisitor visitor) {
    visitor.visitInDataSet(this);
  }

  @Override
  public boolean evaluate() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Expression copy() {
    return new ExpressionInDataSet(operand, dataSetEntry);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(operand.toString());
    builder.append(StringConstant.SPACE);
    builder.append(isNegate() ? ExpressionIn.NOT_IN : ExpressionIn.IN);
    builder.append(StringConstant.SPACE);
    builder.append(StringConstant.LEFT_CURLY);
    builder.append(dataSetEntry);
    builder.append(StringConstant.RIGHT_CURLY);
    return builder.toString();
  }

  public final Operand<?> getOperand() {
    return operand;
  }

  public final void setOperand(Operand<?> operand) {
    this.operand = operand;
  }

  public final DataSetEntry getDataSetEntry() {
    return dataSetEntry;
  }

  public final void setDataSetEntry(DataSetEntry dataSetEntry) {
    this.dataSetEntry = dataSetEntry;
  }

}
