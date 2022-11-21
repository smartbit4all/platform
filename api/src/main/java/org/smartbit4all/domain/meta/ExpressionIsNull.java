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
 * The is null expression tests if the given operand is null or not.
 * 
 * @author Peter Boros
 *
 */
public final class ExpressionIsNull extends Expression {

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
  public Expression copy() {
    return new ExpressionIsNull(this);
  }

  /**
   * The copy constructor.
   * 
   * @param exp
   */
  public ExpressionIsNull(ExpressionIsNull exp) {
    this.op = exp.op;
    this.setNegate(exp.isNegate());
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
