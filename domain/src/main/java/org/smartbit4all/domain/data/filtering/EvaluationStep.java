package org.smartbit4all.domain.data.filtering;

import java.util.List;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;

/**
 * This is one step of the evaluation of an expression tree. It is created by the
 * {@link SB4ExpressionEvaluationVisitor} using the available indices of the data tables behind.
 * Therefore the plan and the items belong to the given data table instance. It doesn't make any
 * sense to reuse it!
 * <p>
 * The possible kind of steps are the following:
 * <ul>
 * <li>{@link EvaluationLoop} : This is a simple cycle through the input indices and evaluate the
 * expression for every row. If there is no index to use then this is "full table scan" in the
 * memory table. For smaller tables it's quite perfect.</li>
 * <li>{@link EvaluationIndexedNonUnique} : This step is based on an index of a given column. The
 * and section can be optimized by using this index for first and use the loop at the end. If we
 * have index then we always use it like a rule based optimizer in the database.</li>
 * <li>{@link EvaluationConcurrent} : This step contains other steps that can be executed parallel
 * using the parallel execution framework of java.</li>
 * </ul>
 * </p>
 * 
 * @author Peter Boros
 * 
 */
abstract class EvaluationStep {

  /**
   * The given evaluation step is going to be executed.
   * 
   * @param tableData The data table that is the basic data set for the evaluation.
   * @param rows The input rows for the evaluation.
   * @return The result row set that contains the rows that are matching the evaluation.
   */
  abstract List<DataRow> execute(TableData<?> tableData, List<DataRow> rows);

  protected abstract void print(StringBuilder buffer, String prefix, String childrenPrefix);

}
