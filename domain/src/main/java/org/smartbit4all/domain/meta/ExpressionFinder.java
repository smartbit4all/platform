package org.smartbit4all.domain.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Finds an expression
 * 
 * @author Peter Boros
 */
public class ExpressionFinder extends ExpressionVisitor {

  /**
   * The expression tester.
   */
  private Predicate<Expression> checkExpression;

  public static class FoundExpression {

    public ExpressionContainer parent;

    public Expression expression;

    public FoundExpression(ExpressionContainer parent, Expression expression) {
      super();
      this.parent = parent;
      this.expression = expression;
    }

  }

  private List<FoundExpression> results = Collections.emptyList();

  public ExpressionFinder(Predicate<Expression> checkExpression) {
    super();
    this.checkExpression = checkExpression;
  }

  private void addResult(ExpressionContainer parent, Expression expression) {
    if (results.isEmpty()) {
      results = new ArrayList<>();
    }
    results.add(new FoundExpression(parent, expression));
  }

  @Override
  public <T> void visit2Operand(Expression2Operand<T> expression) {
    doVisit(expression);
  }

  @Override
  public void visitBetween(ExpressionBetween<?> expression) {
    doVisit(expression);
  }

  @Override
  public void visitBoolean(ExpressionBoolean expression) {
    doVisit(expression);
  }

  @Override
  public void visitBracketPre(ExpressionBracket expression) {
    doVisit(expression);
  }

  @Override
  public void visitClause(ExpressionClause expression) {
    doVisit(expression);
  }

  @Override
  public void visitExists(ExpressionExists expression) {
    doVisit(expression);
  }

  @Override
  public <T> void visitIn(ExpressionIn<T> expression) {
    doVisit(expression);
  }

  @Override
  public void visitIsNull(ExpressionIsNull expression) {
    doVisit(expression);
  }

  private final void doVisit(Expression expression) {
    if (checkExpression.test(expression)) {
      addResult(getParent(), expression);
    }
  }

  public static final List<FoundExpression> find(Expression exp, Predicate<Expression> tester) {
    ExpressionFinder expressionFinder = new ExpressionFinder(tester);
    exp.accept(expressionFinder);
    return expressionFinder.getResults();
  }

  public final List<FoundExpression> getResults() {
    return results;
  }

}
