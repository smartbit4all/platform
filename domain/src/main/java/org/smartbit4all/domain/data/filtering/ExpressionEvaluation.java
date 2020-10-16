package org.smartbit4all.domain.data.filtering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.index.NonUniqueIndex;
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
public final class ExpressionEvaluation extends ExpressionVisitor {

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
   * The index set of the columns for the better performance.
   */
  private TableDataIndexSet indexSet;

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
   * @param currentPlan
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
    finalContext.mergeToPlan(rootPlan);
  }

  @Override
  public void visitBoolean(ExpressionBoolean expression) {
    // We don't do anything because this is a constant expression. If it's true then we skip it. If
    // false then there is no need to execute the embedding loop.
    // TODO implement the skip later on.
    getCurrentPlan().addLoopExpressionStep(expression);
  }


  @Override
  public void visitBetween(ExpressionBetween<?> expression) {
    getCurrentPlan().addLoopExpressionStep(expression);
  }

  @Override
  public void visitIsNull(ExpressionIsNull expression) {
    getCurrentPlan().addLoopExpressionStep(expression);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void visitIn(ExpressionIn<T> expression) {
    Operand<?> operand = expression.getOperand();
    if (operand instanceof OperandProperty<?>) {
      OperandProperty<?> propertyOperand = (OperandProperty<?>) operand;
      constructEvaluationStep(propertyOperand, expression, new ArrayList<>(expression.values()));
    }
  }

  @SuppressWarnings("unchecked")
  private <T> void constructEvaluationStep(OperandProperty<?> propertyOperand,
      Expression expression, List<T> values) {
    TableDataIndex index = null;
    if (indexSet != null) {
      // First try to find the unique index.
      index = indexSet.find(IndexType.UNIQUE, propertyOperand.property());
      if (index != null) {
        getCurrentPlan().addStep(
            new EvaluationIndexedUnique<T>((UniqueIndex<T>) index,
                values, expression.isNegate()));
      } else {
        // Else let's try to find the non unique index.
        index = indexSet.find(IndexType.NONUNIQUE, propertyOperand.property());
        if (index != null) {
          getCurrentPlan().addStep(
              new EvaluationIndexedNonUnique<T>((NonUniqueIndex<T>) index,
                  values, expression.isNegate()));
        } else {
          // If we don't have any index then the fall back is a simple loop.
          getCurrentPlan().addLoopExpressionStep(expression);
        }
      }
    } else {
      // If we don't have any index then the fall back is a simple loop.
      getCurrentPlan().addLoopExpressionStep(expression);
    }
  }

  @Override
  public <T> void visit2Operand(Expression2Operand<T> expression) {
    // We can use the index if we have equal in the operator
    if (expression.operator() == Operator.EQ) {
      constructEvaluationStep(expression.getOp(), expression,
          Arrays.asList(expression.getLiteral().value()));
    } else {
      getCurrentPlan().addLoopExpressionStep(expression);
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
    innerBracketContext.mergeToPlan(outerBracketContext.getCurrentPlan());
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
