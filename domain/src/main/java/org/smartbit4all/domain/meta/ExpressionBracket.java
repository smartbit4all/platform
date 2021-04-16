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
package org.smartbit4all.domain.meta;

/**
 * The bracket contains an expression tree by its root element. It can be negated to negate the
 * whole underlying tree.
 * 
 * @author Peter Boros
 *
 */
public final class ExpressionBracket extends Expression implements ExpressionContainer {

  /**
   * The inner condition inside the bracket.
   */
  private Expression expression;

  public ExpressionBracket(Expression expression) {
    super();
    this.expression = expression;
  }

  @Override
  public Expression copy() {
    return new ExpressionBracket(this);
  }

  /**
   * The copy constructor.
   * 
   * @param exp
   */
  public ExpressionBracket(ExpressionBracket exp) {
    this.expression = exp.expression != null ? exp.expression.copy() : null;
    this.setNegate(exp.isNegate());
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
    visitor.setParent(this);
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

  @Override
  public boolean replace(Expression oldExp, Expression newExp) {
    if (expression == oldExp) {
      expression = newExp;
      return true;
    }
    return false;
  }

}
