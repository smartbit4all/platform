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
package org.smartbit4all.domain.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * It produce the list of all the properties that appear in the expressions.
 * 
 * @author Zoltan Suller
 */
public final class ExpressionPropertyCollector extends ExpressionVisitor {

  /**
   * This builder of the final textual representation.
   */
  List<Property<?>> properties = new ArrayList<>();

  public ExpressionPropertyCollector() {
    super();
  }

  protected <T> void addIfOperandProperty(Operand<T> operand) {
    if (operand instanceof OperandProperty) {
      properties.add(((OperandProperty) operand).property());
    }
  }

  @Override
  public <T> void visit2Operand(Expression2Operand<T> expression) {
    addIfOperandProperty(expression.getOp());
  }

  @Override
  public void visitBetween(ExpressionBetween<?> expression) {
    addIfOperandProperty(expression.getOperand());
  }

  @Override
  public <T> void visitIn(ExpressionIn<T> expression) {
    addIfOperandProperty(expression.getOperand());
  }

  @Override
  public void visitIsNull(ExpressionIsNull expression) {
    addIfOperandProperty(expression.getOp());
  }

  @Override
  public void visitInDataSet(ExpressionInDataSet expressionInDataSet) {
    addIfOperandProperty(expressionInDataSet.getOperand());
  }

  public List<Property<?>> getProperties() {
    return properties;
  }

}
