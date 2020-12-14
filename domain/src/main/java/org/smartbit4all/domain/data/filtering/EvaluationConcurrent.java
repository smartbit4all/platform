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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;

/**
 * This step is responsible for executing the OR sections parallel to each other. At the end this
 * constructs the union of the results.
 * 
 * @author Peter Boros
 *
 */
public class EvaluationConcurrent extends EvaluationStep {

  /**
   * The plans that can be executed parallel.
   */
  List<ExpressionEvaluationPlan> plans = new ArrayList<>();

  /**
   * The list of the result rows for each plan.
   */
  List<List<DataRow>> resultLists = new ArrayList<>();

  Lock lockResult = new ReentrantLock();

  @Override
  List<DataRow> execute(TableData<?> tableData, List<DataRow> rows) {
    resultLists.clear();
    // We can execute the two different branch individually and union the results. It's a parallel
    // execution to utilize the available resource to achieve better performance.
    plans.parallelStream().forEach(p -> {
      List<DataRow> result = p.execute(rows);
      lockResult.lock();
      try {
        resultLists.add(result);
      } finally {
        lockResult.unlock();
      }
    });

    return resultLists.stream().flatMap(List::stream).distinct().collect(Collectors.toList());
  }
  
  void addPlan(ExpressionEvaluationPlan plan) {
    plans.add(plan);
  }
  
  public List<ExpressionEvaluationPlan> getPlans() {
    return plans;
  }
  
  @Override
  protected void print(StringBuilder buffer, String prefix, String childrenPrefix) {
    buffer.append(prefix);
    buffer.append("EvaluationConcurrent");
    buffer.append('\n');
    
    for (Iterator<ExpressionEvaluationPlan> it = plans.iterator(); it.hasNext();) {
      ExpressionEvaluationPlan next = it.next();
      if (it.hasNext()) {
        next.print(buffer, childrenPrefix + "|- ", childrenPrefix + "|   ");
      } else {
        next.print(buffer, childrenPrefix + "\\- ", childrenPrefix + "    ");
        buffer.append(childrenPrefix + "\n");
      }
    }
  }

}
