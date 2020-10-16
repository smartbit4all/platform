package org.smartbit4all.domain.meta;

/**
 * The bracket contains an expression tree by its root element. It can be negated to negate the
 * whole underlying tree.
 * 
 * @author Peter Boros
 *
 */
public class ExpressionBracket extends Expression {

  /**
   * The inner condition inside the bracket.
   */
  private Expression expression;

  public ExpressionBracket(Expression expression) {
    super();
    this.expression = expression;
  }

  public final Expression getExpression() {
    return expression;
  }

  public final void setExpression(Expression expression) {
    this.expression = expression;
  }

  @Override
  public boolean evaluate() {
    return isNegate() ? !expression.evaluate() : expression.evaluate();
  }

  @Override
  public final void accept(ExpressionVisitor visitor) {
    visitor.visitBracketPre(this);
    expression.accept(visitor);
    visitor.visitBracketPost(this);
  }

  /**
   * Overridden to avoid unnecessary cast.
   * 
   * @return This condition for the fluid API.
   */
  @Override
  public final ExpressionBracket NOT() {
    setNegate(!isNegate());
    return this;
  }

}
