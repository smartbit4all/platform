/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.data.filtering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.smartbit4all.domain.data.TableData;

/**
 * Helps with the processing of an expression. Contains the result
 * {@link ExpressionEvaluationPlan}s, and the currently active plan during the processing.
 * 
 * @author Zoltán Suller
 *
 */
public class ExpressionPartEvaluationContext {

  /**
   * The {@link ExpressionEvaluationPlan}s created for the expression.
   */
  private List<ExpressionEvaluationPlan> plans = new ArrayList<ExpressionEvaluationPlan>();

  /**
   * The active plan we are working on during the processing.
   */
  private ExpressionEvaluationPlan currentPlan;

  ExpressionPartEvaluationContext(TableData<?> tableData) {
    createNewPlan(tableData);
  }

  /**
   * Creates a new plan for the context. We call this when we find an OR operator.
   * 
   * @param tableData
   */
  void createNewPlan(TableData<?> tableData) {
    currentPlan = new ExpressionEvaluationPlan(tableData);
    plans.add(currentPlan);
  }

  /**
   * @return the active plan of the context
   */
  ExpressionEvaluationPlan getCurrentPlan() {
    return currentPlan;
  }

  /**
   * Merge the plans of the context to another plan. We call this when we processed the expression
   * and want to merge the result to the parent expression's plan.
   * 
   * @param parentExpressionPlan
   */
  void mergeToPlan(ExpressionEvaluationPlan parentExpressionPlan) {
    unfoldPlans();
    // If the context has more than one plan then we create an EvaluationConcurrent with the plans,
    // and add this step to the parameter plan.
    if (plans.size() > 1) {
      EvaluationConcurrent step = new EvaluationConcurrent();
      for (ExpressionEvaluationPlan plan : plans) {
        step.addPlan(plan);
      }
      parentExpressionPlan.addStep(step);
    } else {
      // If there is only one plan then we add our plan's steps to the parameter plan.
      for (EvaluationStep step : plans.get(0).getSteps()) {
        parentExpressionPlan.addStep(step);
      }
    }
  }

  /**
   * If a plan has only one {@link EvaluationConcurrent} step, then we can open that step and add
   * the plans of the {@link EvaluationConcurrent} to the context's plans. For example
   * <p>
   * Expression A || ( B || C ) <br>
   * After we processed the ( B || C ) bracket we and merge it the the parent context, the parent
   * context will have 2 plans:
   * <ul>
   * <li>First plan will have steps to handle the expression A.</li>
   * <li>Second plan will have an {@link EvaluationConcurrent} with 2 plans to handle expression B
   * and expression C.</li>
   * </ul>
   * In this case we can add the plans inside the {@link EvaluationConcurrent} to the main context,
   * so it will have 3 plans.
   */
  private void unfoldPlans() {
    List<ExpressionEvaluationPlan> plansToAdd = new ArrayList<ExpressionEvaluationPlan>();
    Iterator<ExpressionEvaluationPlan> iterator = plans.iterator();
    while (iterator.hasNext()) {
      ExpressionEvaluationPlan plan = iterator.next();
      if (plan.getSteps().size() == 1 && plan.getSteps().get(0) instanceof EvaluationConcurrent) {
        EvaluationConcurrent concStep = (EvaluationConcurrent) plan.getSteps().get(0);
        plansToAdd.addAll(concStep.getPlans());
        iterator.remove();
      }
    }
    if (!plansToAdd.isEmpty()) {
      plans.addAll(plansToAdd);
    }
  }
}
