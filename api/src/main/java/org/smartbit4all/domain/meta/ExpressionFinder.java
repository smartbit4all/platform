package org.smartbit4all.domain.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    private ExpressionContainer parent;

    private Expression expression;

    private List<Expression> path;

    public FoundExpression(ExpressionContainer parent, Expression expression,
        List<Expression> path) {
      super();
      this.parent = parent;
      this.expression = expression;
      this.path = path;
    }

    public final ExpressionContainer getParent() {
      return parent;
    }

    public final Expression getExpression() {
      return expression;
    }

    public final List<Expression> getPath() {
      return path;
    }

  }

  private List<FoundExpression> results = Collections.emptyList();

  public ExpressionFinder(Predicate<Expression> checkExpression) {
    super();
    this.checkExpression = checkExpression;
  }

  private void addResult(ExpressionContainer parent, Expression expression, List<Expression> list) {
    if (results.isEmpty()) {
      results = new ArrayList<>();
    }
    results
        .add(new FoundExpression(parent, expression, list.stream().collect(Collectors.toList())));
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
      addResult(getParent(), expression, getExpressionStack());
    }
  }

  public static final List<FoundExpression> find(Expression exp, Predicate<Expression> tester) {
    ExpressionFinder expressionFinder = new ExpressionFinder(tester);
    exp.accept(expressionFinder);
    return expressionFinder.getResults();
  }

  /**
   * This utility is searching for the largest conjunctive clause that contains the exp expression.
   * We are looking for the first OR clause and we can't find any then we return the root.
   * 
   * @param root The root expression where we can navigate.
   * @param exp The expression that we are looking for.
   * @return The Expression that is the largest clause that is restrictive all the expression is
   *         related with AND.
   */
  public static final Expression findLargestConjunctiveClause(Expression root, Expression exp) {
    ListIterator<FoundExpression> find = ExpressionFinder.find(root, e -> e == exp).listIterator();
    if (find.hasNext()) {
      FoundExpression foundExpression = find.next();
      ListIterator<Expression> iterPath =
          foundExpression.getPath().listIterator(foundExpression.getPath().size());
      Expression candidate = exp;
      while (iterPath.hasPrevious()) {
        Expression expression = iterPath.previous();
        if (expression instanceof ExpressionClause) {
          // If we have an OR then we return the candidate.
          if (((ExpressionClause) expression).getOperator() == BooleanOperator.OR) {
            return candidate;
          }
        }
        candidate = expression;
      }
      return candidate;
    }
    return exp;
  }

  public final List<FoundExpression> getResults() {
    return results;
  }

}
