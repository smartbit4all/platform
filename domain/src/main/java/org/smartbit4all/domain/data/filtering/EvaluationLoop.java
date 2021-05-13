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

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.index.StorageLoader;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.ExpressionBetween;
import org.smartbit4all.domain.meta.ExpressionIn;
import org.smartbit4all.domain.meta.ExpressionIsNull;
import org.smartbit4all.domain.meta.ExpressionVisitor;
import org.smartbit4all.domain.meta.Operand;
import org.smartbit4all.domain.meta.OperandProperty;

/**
 * For an expression that could be evaluated in a single loop row by row.
 * 
 * @author Peter Boros
 *
 */
public class EvaluationLoop extends EvaluationStep {

  /**
   * The expression that must be evaluated for every row.
   */
  private Expression expression;

  /**
   * If we have a loader then this loop can load the data into the table data before the evaluation.
   */
  private StorageLoader loader = null;

  EvaluationLoop(Expression expression, StorageLoader loader) {
    super();
    this.expression = expression;
    this.loader = loader;
  }

  // EvaluationLoop(Expression expression) {
  // super();
  // this.expression = expression;
  // }

  Expression getExpression() {
    return expression;
  }

  void addExpression(Expression exp) {
    expression = expression.AND(exp);
  }

  @Override
  List<DataRow> execute(TableData<?> tableData, List<DataRow> rows) {
    List<DataRow> rowsToEvaluate = rows;
    if (loader != null) {
      if (rowsToEvaluate == null) {
        // We have doesn't have row list so we must load all the rows available on the storage.
        loader.loadAllRows(tableData);
        // evaluate the whole table data
        rowsToEvaluate = tableData.rows();
      } else {
        loader.fillRows(tableData, rowsToEvaluate);
      }
    }
    // At the very beginning we need to bind the columns of the expressions inside. We need to visit
    // every expression to set the bound value for them.
    List<BoundValueColumn<?>> boundValues = constructBoundValues(tableData);
    List<DataRow> result = new ArrayList<>();
    for (DataRow row : rowsToEvaluate) {
      for (BoundValueColumn<?> boundValue : boundValues) {
        boundValue.setRow(row);
      }
      if (expression.evaluate()) {
        result.add(row);
      }
    }
    return result;
  }

  /**
   * This operation explore the expression tree to set and collect the bound values.
   * 
   * @return The list of bound values.
   */
  private List<BoundValueColumn<?>> constructBoundValues(TableData<?> tableData) {
    List<BoundValueColumn<?>> result = new ArrayList<>();
    expression.accept(new ExpressionVisitor() {

      @Override
      public void visitIsNull(ExpressionIsNull expression) {
        createBoundValue(tableData, result, expression.getOp());
      }

      @Override
      public <T> void visitIn(ExpressionIn<T> expression) {
        createBoundValue(tableData, result, expression.getOperand());
      }

      @Override
      public void visitBetween(ExpressionBetween<?> expression) {
        createBoundValue(tableData, result, expression.getOperand());
        createBoundValue(tableData, result, expression.getLowerBound());
        createBoundValue(tableData, result, expression.getUpperBound());
      }

      @Override
      public <T> void visit2Operand(Expression2Operand<T> expression) {
        createBoundValue(tableData, result, expression.getOp());
      }
    });
    return result;
  }

  private void createBoundValue(TableData<?> tableData, List<BoundValueColumn<?>> result,
      Operand<?> op) {
    if (op instanceof OperandProperty<?>) {
      OperandProperty<?> operandProperty = (OperandProperty<?>) op;
      BoundValueColumn<?> boundValueColumn =
          new BoundValueColumn<>(tableData, tableData.getColumn(operandProperty.property()));
      result.add(boundValueColumn);
      operandProperty.bind(boundValueColumn);
    }
  }

  @Override
  protected void print(StringBuilder buffer, String prefix, String childrenPrefix) {
    buffer.append(prefix);
    buffer.append("EvaluationLoop (" + expression.toString() + ")");
    buffer.append('\n');
  }

}
