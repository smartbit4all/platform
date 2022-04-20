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
import java.util.Iterator;
import java.util.List;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.index.StorageLoader;
import org.smartbit4all.domain.data.index.TableDataIndexSet;
import org.smartbit4all.domain.meta.Expression;

/**
 * <p>
 * This the ordered list of evaluation items to be executed for the most optimal filtering of a data
 * table. It works on the row lists and during the filtering and uses union, intersect and other set
 * operations. This object can be created by the {@link ExpressionEvaluation}.
 * 
 * <p>
 * The plan is created the following way:
 * <ul>
 * <li>We are processing the expression from left to right.</li>
 * <li>If we find an OR operator, we create an {@link EvaluationConcurrent} step with the expression
 * A and expression B as plans inside the step.</li>
 * <li>If we find an AND operator, we add expression A and expression B as steps to the same
 * plan.</li>
 * </ul>
 * 
 * 
 * <p>
 * Example 1 <br>
 * 
 * <pre>
 * A && ( B && C || D && E)
 * 
 * Plan
 * |- EvaluationLoop (A)
 * \- EvaluationConcurrent
 *      |- Plan
 *      |   \- EvaluationLoop (B && C)
 *      |
 *      \- Plan
 *          \- EvaluationLoop (D && E)
 * </pre>
 * <p>
 * Example 2 <br>
 * 
 * <pre>
 * A || ( B || C || D) && E
 * 
 * Plan
 * \- EvaluationConcurrent
 *     |- Plan
 *     |   \- EvaluationLoop (A)
 *     |
 *     \- Plan
 *        |- EvaluationConcurrent
 *        |   |- Plan
 *        |   |   \- EvaluationLoop (B)
 *        |   |
 *        |   |- Plan
 *        |   |   \- EvaluationLoop (C)
 *        |   |
 *        |   \- Plan
 *        |       \- EvaluationLoop (D)
 *        |
 *        |
 *        \- EvaluationLoop (E)
 * </pre>
 * 
 * @author Peter Boros
 *
 */
public class ExpressionEvaluationPlan {

  /**
   * The data table that will be filtered by evaluating the expression.
   */
  private final TableData<?> tableData;

  /**
   * The steps of the evaluation process.
   */
  private final List<EvaluationStep> steps = new ArrayList<>();

  /**
   * The loop step of the plan. Since the loop step can handle multiple expressions, we only need
   * one loop step.
   */
  private EvaluationLoop loopStep;

  /**
   * Constructs a new plan attached to the given data table.
   * 
   * @param tableData The data table that is the subject of the filtering.
   */
  ExpressionEvaluationPlan(TableData<?> tableData) {
    super();
    this.tableData = tableData;
  }

  /**
   * The data table.
   * 
   * @return
   */
  public final TableData<?> getTableData() {
    return tableData;
  }

  /**
   * The list of steps that were created by the visitor.
   * 
   * @return
   */
  public final List<EvaluationStep> getSteps() {
    return steps;
  }

  /**
   * Add the step to the steps of the plan. If the step is a Loop step, then we fist check if the
   * plan has a loop step already.
   * 
   * @param step
   */
  public void addStep(EvaluationStep step, StorageLoader loader) {
    if (step instanceof EvaluationLoop) {
      addLoopExpressionStep(((EvaluationLoop) step).getExpression(), loader);
    } else {
      steps.add(step);
    }
  }

  /**
   * If the current plan has a loop step already, then we add the expression to the loop step. If
   * not, we create a new loop step with the expression
   * 
   * @param expression
   */
  public void addLoopExpressionStep(Expression expression, StorageLoader loader) {
    if (loopStep == null) {
      loopStep = new EvaluationLoop(expression, loader);
      steps.add(loopStep);
    } else {
      loopStep.addExpression(expression);
    }
  }

  /**
   * Execute the evaluation plan in a sequential order. The {@link EvaluationConcurrent} steps will
   * define embedded execution plans. In a "normal" expression where we don't have any OR and we
   * don't have any index on the properties. In this case there is only one single step in the plan.
   * One loop to test the expression row by row. If we have one or more indexed column then these
   * indexed evaluation steps will be the first steps (rule based, because we always use the index
   * to filter) and then the loop again with the matching rows.
   * 
   * If we have an OR (with or without a bracket) among the ANDs then it means a concurrent
   * evaluation plan. Both operand of the OR will be evaluated and the parts will be unified at the
   * end.
   * 
   * @param initialRows The initial rows for the evaluation. This is a sorted (by position) list of
   *        rows.
   * @return The matching rows after execution also in a sorted list.
   */
  public List<DataRow> execute(List<DataRow> initialRows) {
    if (steps.isEmpty()) {
      // In this case we don't have to execute
      return initialRows;
    }
    List<DataRow> result = null;
    for (EvaluationStep step : steps) {
      List<DataRow> matchingRows =
          step.execute(tableData, getNextStepBaseRows(initialRows, result));
      if (matchingRows != null) {
        result = matchingRows;
      }
    }
    return result;
  }

  /**
   * Execute the evaluation plan in a sequential order. The {@link EvaluationConcurrent} steps will
   * define embedded execution plans. In a "normal" expression where we don't have any OR and we
   * don't have any index on the properties. In this case there is only one single step in the plan.
   * One loop to test the expression row by row. If we have one or more indexed column then these
   * indexed evaluation steps will be the first steps (rule based, because we always use the index
   * to filter) and then the loop again with the matching rows.
   * 
   * If we have an OR (with or without a bracket) among the ANDs then it means a concurrent
   * evaluation plan. Both operand of the OR will be evaluated and the parts will be unified at the
   * end.
   * 
   * @return The matching rows after execution also in a sorted list.
   */
  public List<DataRow> execute() {
    return execute(tableData.rows());
  }

  /**
   * If the result is NULL, it is the first step. - If there is any initial rows, use them as base.
   * - Else NULL is passed, to create a new base from the first step execution.
   */
  private List<DataRow> getNextStepBaseRows(List<DataRow> initialRows, List<DataRow> result) {
    if (result == null && initialRows != null && !initialRows.isEmpty()) {
      return initialRows;
    }
    return result;
  }

  /**
   * This is recursive function that traverse the expression tree to produce the most optimal plan
   * for the evaluation. For this it receives a data table index structure that can help to optimize
   * the filtering. The index must be created for the data table itself, the references and the
   * referred data tables also. In a complex situation we can use the index for a referred data
   * table to filter the matching rows. Later on we can use these rows as input for filtering the
   * referrer rows from the current data table.
   * 
   * @param tableData The data table to bind with.
   * @param indexSet The index set that can be used to optimize the evaluation process.
   * @param expression The expression to evaluate.
   * @return The plan that can be executed.
   */
  public static final ExpressionEvaluationPlan of(TableData<?> tableData,
      TableDataIndexSet indexSet, Expression expression) {
    return new ExpressionEvaluation(tableData, indexSet, expression).plan();
  }

  /**
   * This is recursive function that traverse the expression tree to produce the most optimal plan
   * for the evaluation. For this it receives a data table index structure that can help to optimize
   * the filtering. The index must be created for the data table itself, the references and the
   * referred data tables also. In a complex situation we can use the index for a referred data
   * table to filter the matching rows. Later on we can use these rows as input for filtering the
   * referrer rows from the current data table.
   * 
   * @param loader The storage loader that is responsible for loading the necessary rows into the
   *        table data the evaluation is working on.
   * @param expression The expression to evaluate.
   * @return The plan that can be executed.
   */
  public static final ExpressionEvaluationPlan of(StorageLoader loader, Expression expression) {
    return new ExpressionEvaluation(loader, expression).plan();
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder(50);
    print(buffer, "", "");
    return buffer.toString();
  }

  void print(StringBuilder buffer, String prefix, String childrenPrefix) {
    buffer.append(prefix);
    buffer.append("Plan");
    buffer.append('\n');
    for (Iterator<EvaluationStep> it = steps.iterator(); it.hasNext();) {
      EvaluationStep next = it.next();
      if (it.hasNext()) {
        next.print(buffer, childrenPrefix + "|- ", childrenPrefix + "|   ");
      } else {
        next.print(buffer, childrenPrefix + "\\- ", childrenPrefix + "    ");
        buffer.append(childrenPrefix + "\n");
      }
    }
  }

}
