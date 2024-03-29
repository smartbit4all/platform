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
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.index.NonUniqueIndex;
import org.smartbit4all.domain.data.index.StorageIndex;
import org.smartbit4all.domain.data.index.StorageLoader;
import org.smartbit4all.domain.data.index.StorageNonUniqueIndex;
import org.smartbit4all.domain.data.index.TableDataIndex;
import org.smartbit4all.domain.data.index.TableDataIndex.IndexType;
import org.smartbit4all.domain.data.index.TableDataIndexSet;
import org.smartbit4all.domain.data.index.UniqueIndex;
import org.smartbit4all.domain.meta.BooleanOperator;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.Expression2Operand.Operator;
import org.smartbit4all.domain.meta.ExpressionBetween;
import org.smartbit4all.domain.meta.ExpressionBoolean;
import org.smartbit4all.domain.meta.ExpressionBracket;
import org.smartbit4all.domain.meta.ExpressionClause;
import org.smartbit4all.domain.meta.ExpressionIn;
import org.smartbit4all.domain.meta.ExpressionIsNull;
import org.smartbit4all.domain.meta.ExpressionVisitor;
import org.smartbit4all.domain.meta.Operand;
import org.smartbit4all.domain.meta.OperandProperty;

/**
 * This visitor is a helper class to traverse the expression tree and produce an evaluation plan.
 * 
 * @author Peter Boros
 *
 */
final class ExpressionEvaluation extends ExpressionVisitor {

  /**
   * The actual plan that is being processed. If we collect all the expressions in the sub tree into
   * a single loop then we can add the loop at post time.
   */
  private ExpressionEvaluationPlan rootPlan;

  /**
   * The data table the evaluation is based on.
   */
  private TableData<?> tableData;

  /**
   * The index set of the columns for the better performance. These indices are based on the already
   * loaded data in the {@link #tableData}.
   */
  private TableDataIndexSet indexSet;

  /**
   * This loader is able to add all the rows available in the storage and can fill the missing
   * property values in the table data for a given set of row.
   */
  private StorageLoader loader;

  /**
   * The expressionPartEvaluationContextStack contains the evaluation plans for the sub parts of the
   * expression (brackets). After we finished processing a bracket we merge the plans of the context
   * to the parent context.
   */
  private Stack<ExpressionPartEvaluationContext> expressionPartEvaluationContextStack =
      new Stack<ExpressionPartEvaluationContext>();

  /**
   * Initiate the visitor for the traverse algorithm.
   * 
   */
  public ExpressionEvaluation(TableData<?> tableData, TableDataIndexSet indexSet,
      Expression expression) {
    super();
    this.tableData = tableData;
    this.indexSet = indexSet != null ? indexSet : tableData.index();

    // We create a context for the whole expression.
    expressionPartEvaluationContextStack.push(new ExpressionPartEvaluationContext(tableData));

    expression.accept(this);

    // After we processed the expression we merge the context's plans to the rootplan.
    ExpressionPartEvaluationContext finalContext = expressionPartEvaluationContextStack.pop();
    rootPlan = new ExpressionEvaluationPlan(tableData);
    finalContext.mergeToPlan(rootPlan, loader);
  }

  /**
   * Initiate the visitor for the traverse algorithm.
   * 
   */
  public ExpressionEvaluation(
      StorageLoader loader,
      Expression expression) {

    super();
    this.loader = loader;
    // The table data will be constructed by the meta data of the loader and the expression.
    this.tableData = TableDatas.builder(
        loader.getEntityDef(),
        loader.getEntityDef().allProperties()).build();

    // We create a context for the whole expression.
    expressionPartEvaluationContextStack.push(new ExpressionPartEvaluationContext(tableData));

    expression.accept(this);

    // After we processed the expression we merge the context's plans to the rootplan.
    ExpressionPartEvaluationContext finalContext = expressionPartEvaluationContextStack.pop();
    rootPlan = new ExpressionEvaluationPlan(tableData);
    finalContext.mergeToPlan(rootPlan, loader);
  }

  @Override
  public void visitBoolean(ExpressionBoolean expression) {
    // We don't do anything because this is a constant expression. If it's true then we skip it. If
    // false then there is no need to execute the embedding loop.
    // TODO implement the skip later on.
    getCurrentPlan().addLoopExpressionStep(expression, loader);
  }


  @Override
  public void visitBetween(ExpressionBetween<?> expression) {
    getCurrentPlan().addLoopExpressionStep(expression, loader);
  }

  @Override
  public void visitIsNull(ExpressionIsNull expression) {
    getCurrentPlan().addLoopExpressionStep(expression, loader);
  }

  @Override
  public <T> void visitIn(ExpressionIn<T> expression) {
    Operand<?> operand = expression.getOperand();
    if (operand instanceof OperandProperty<?>) {
      OperandProperty<?> propertyOperand = (OperandProperty<?>) operand;
      constructEvaluationStep(propertyOperand, expression, new ArrayList<>(expression.values()));
    }
  }

  private <T> void constructEvaluationStep(
      OperandProperty<?> propertyOperand,
      Expression expression,
      List<T> values) {

    boolean indexUsed = tryUseIndex(propertyOperand, expression, values);

    // If we don't have any index then the fall back is a simple loop.
    if (!indexUsed) {
      getCurrentPlan().addLoopExpressionStep(expression, loader);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> boolean tryUseIndex(OperandProperty<?> propertyOperand, Expression expression,
      List<T> values) {

    TableDataIndex index = null;
    if (indexSet != null) {
      // First try to find the unique index.
      index = indexSet.find(IndexType.UNIQUE, propertyOperand.property());
      if (index != null) {
        getCurrentPlan().addStep(
            new EvaluationIndexedUnique<T>((UniqueIndex<T>) index,
                values, expression.isNegate()),
            loader);
      } else {
        // Else let's try to find the non unique index.
        index = indexSet.find(IndexType.NONUNIQUE, propertyOperand.property());
        if (index != null) {
          getCurrentPlan().addStep(
              new EvaluationIndexedNonUnique<T>((NonUniqueIndex<T>) index,
                  values, expression.isNegate()),
              loader);
        } else {
          // If we don't have any index then the fall back is a simple loop.
          getCurrentPlan().addLoopExpressionStep(expression, loader);
        }
      }
      return true;

    } else if (loader != null) {

      // If we have a loader with storage indices we try to construct indexed steps. Now we use the
      // first available index.
      boolean storageIndexUsed = false;
      for (StorageIndex idx : loader.getIndices()) {

        // TODO this raw cast should be replaced, maybe the StorageIndex should return sa
        if (idx.canUseFor(expression)) {
          getCurrentPlan().addStep(

              new EvaluationIndexedNonUniqueStorage<T>(
                  loader,
                  (StorageNonUniqueIndex<T, ?>) idx,
                  values,
                  expression.isNegate()),

              loader);

          storageIndexUsed = true;
        }

      }
      return storageIndexUsed;

    }
    return false;
  }

  @Override
  public <T> void visit2Operand(Expression2Operand<T> expression) {
    // We can use the index if we have equal in the operator
    if (expression.operator() == Operator.EQ) {
      constructEvaluationStep(expression.getOp(), expression,
          Arrays.asList(expression.getLiteral().value()));
    } else {
      getCurrentPlan().addLoopExpressionStep(expression, loader);
    }
  }

  @Override
  public void visitClause(ExpressionClause expression) {
    // A || B
    // ^
    // We are creating a new plan inside the current context.
    if (expression.getOperator() == BooleanOperator.OR) {
      expressionPartEvaluationContextStack.peek().createNewPlan(tableData);
    }
  }

  @Override
  public void visitBracketPre(ExpressionBracket expression) {
    // (A || B && C)
    // ^
    // We are creating a new context to process the bracket expression.
    expressionPartEvaluationContextStack.push(new ExpressionPartEvaluationContext(tableData));
  }

  @Override
  public void visitBracketPost(ExpressionBracket expression) {
    // (A || B && C)
    // ^
    // After we processed the bracket expression, we merge the result plans to the parent
    // expression's context
    ExpressionPartEvaluationContext innerBracketContext =
        expressionPartEvaluationContextStack.pop();
    ExpressionPartEvaluationContext outerBracketContext =
        expressionPartEvaluationContextStack.peek();
    innerBracketContext.mergeToPlan(outerBracketContext.getCurrentPlan(), loader);
  }

  /**
   * Return the plan after the constructor. Can be used like the following:
   * 
   * <p>
   * <code>
   * (new ExpressionEvaluation(tableData, indexSet, expression)).plan().execute();
   * </code>
   * </p>
   * 
   * @return
   */
  public ExpressionEvaluationPlan plan() {
    return rootPlan;
  }

  /**
   * @return the current context's current plan
   */
  public ExpressionEvaluationPlan getCurrentPlan() {
    return expressionPartEvaluationContextStack.peek().getCurrentPlan();
  }
}
