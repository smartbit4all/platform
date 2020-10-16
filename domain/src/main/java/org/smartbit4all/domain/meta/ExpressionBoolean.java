package org.smartbit4all.domain.meta;

/**
 * The boolean expression is a special literal that holds the result of the expression. This is a
 * logical constant.
 * 
 * @author Peter Boros
 *
 */
public class ExpressionBoolean extends Expression {

  /**
   * The value of the logical expression.
   */
  private boolean value;

  /**
   * Creates a new boolean expression.
   * 
   * @param value
   */
  public ExpressionBoolean(boolean value) {
    super();
    this.value = value;
  }

  /**
   * @return Return true if not negated.
   */
  @Override
  public boolean evaluate() {
    return isNegate() ? !value : value;
  }

  @Override
  public final void accept(ExpressionVisitor visitor) {
    visitor.visitBoolean(this);
  }

  /**
   * Overridden to avoid unnecessary cast.
   * 
   * @return This condition for the fluid API.
   */
  @Override
  public final ExpressionBoolean NOT() {
    setNegate(!isNegate());
    return this;
  }

  @Override
  public String toString() {
    return Boolean.toString(evaluate());
  }

}
