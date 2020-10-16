package org.smartbit4all.domain.meta;

/**
 * The is null expression tests if the given operand is null or not.
 * 
 * @author Peter Boros
 *
 */
public class ExpressionIsNull extends Expression {

  public static final String IS_NULL = " IS NULL";
  public static final String IS_NOT_NULL = " IS NOT NULL";
  /**
   * The operand to check.
   */
  private Operand<?> op;

  ExpressionIsNull(Operand<?> op) {
    super();
    this.op = op;
  }

  @Override
  public boolean evaluate() {
    return isNegate() ? op.value() != null : op.value() == null;
  }

  @Override
  public final void accept(ExpressionVisitor visitor) {
    visitor.visitIsNull(this);
  }

  /**
   * Overridden to avoid unnecessary cast.
   * 
   * @return This condition for the fluid API.
   */
  @Override
  public final ExpressionIsNull NOT() {
    setNegate(!isNegate());
    return this;
  }

  public final Operand<?> getOp() {
    return op;
  }

  @Override
  public String toString() {
    return op.toString() + (isNegate() ? IS_NOT_NULL : IS_NULL);
  }

}
