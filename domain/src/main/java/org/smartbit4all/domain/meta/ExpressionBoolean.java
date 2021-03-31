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
 * The boolean expression is a special literal that holds the result of the expression. This is a
 * logical constant.
 * 
 * @author Peter Boros
 *
 */
public final class ExpressionBoolean extends Expression {

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

  @Override
  public Expression copy() {
    return new ExpressionBoolean(this);
  }

  /**
   * The copy constructor.
   * 
   * @param exp
   */
  public ExpressionBoolean(ExpressionBoolean exp) {
    this.value = exp.value;
    this.setNegate(exp.isNegate());
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
