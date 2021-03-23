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

import java.util.Comparator;
import java.util.Objects;
import org.smartbit4all.core.utility.StringConstant;

/**
 * This is for three operand between expression. The between is the normal between from SQL. The
 * symmetric means that it doesn't mater which bound is less or greater. The expression will examine
 * both and if any of these match then the result is true. ([] BETWEEN [B] AND [C]) OR ([A] BETWEEN
 * [C] AND [B])
 * 
 * @author Peter Boros
 *
 */
public class ExpressionBetween<T> extends Expression {

  public static final String BETWEEN = "BETWEEN";

  /**
   * For the symmetric bound check set this true.
   */
  private boolean symmetric = false;

  /**
   * The examined operand.
   */
  private OperandProperty<T> operand;

  /**
   * The lower bound of the between.
   */
  private Operand<T> lowerBound;

  /**
   * The upper bound of the between expression.
   */
  private Operand<T> upperBound;

  /**
   * The comparator to compare the values of the operands.
   */
  private Comparator<T> comparator = null;

  public ExpressionBetween(OperandProperty<T> operand, Operand<T> lowerBound, Operand<T> upperBound,
      boolean symmetric) {
    super();
    this.operand = operand;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.symmetric = symmetric;
  }

  public ExpressionBetween(OperandProperty<T> operand, Operand<T> lowerBound,
      Operand<T> upperBound) {
    this(operand, lowerBound, upperBound, false);
  }

  @Override
  public Expression copy() {
    return new ExpressionBetween<T>(this);
  }

  /**
   * The copy constructor.
   * 
   * @param exp
   */
  public ExpressionBetween(ExpressionBetween<T> exp) {
    this.operand = exp.operand;
    this.lowerBound = exp.lowerBound;
    this.upperBound = exp.upperBound;
    this.symmetric = exp.symmetric;
    this.setNegate(exp.isNegate());
  }

  public final boolean isSymmetric() {
    return symmetric;
  }

  public final void setSymmetric(boolean symmetric) {
    this.symmetric = symmetric;
  }

  public final Operand<T> getOperand() {
    return operand;
  }

  public final void setOperand(OperandProperty<T> operand) {
    this.operand = operand;
  }

  public final Operand<T> getLowerBound() {
    return lowerBound;
  }

  public final void setLowerBound(Operand<T> lowerBound) {
    this.lowerBound = lowerBound;
  }

  public final Operand<T> getUpperBound() {
    return upperBound;
  }

  public final void setUpperBound(Operand<T> upperBound) {
    this.upperBound = upperBound;
  }

  @Override
  public boolean evaluate() {
    return isNegate() ? !myEvaluate() : myEvaluate();
  }

  /**
   * Manages the {@link #symmetric} value.
   * 
   * @return
   */
  private boolean myEvaluate() {
    boolean result = myBetween(lowerBound, upperBound);
    if (symmetric) {
      boolean symResult = myBetween(upperBound, lowerBound);
      result = result || symResult;
    }
    return result;
  }

  /**
   * Implements the between in one direction.
   * 
   * @param myOp1
   * @param myOp2
   * @return
   */
  private boolean myBetween(Operand<T> myOp1, Operand<T> myOp2) {
    int withLower = Objects.compare(myOp1.value(), operand.value(), getComparator());
    int withUpper = Objects.compare(operand.value(), myOp2.value(), getComparator());
    return withLower >= 0 && withUpper >= 0;
  }

  @Override
  public void accept(ExpressionVisitor visitor) {
    visitor.visitBetween(this);
  }

  private final Comparator<T> getComparator() {
    if (comparator == null) {
      comparator = Comparator.nullsFirst(operand.property.getComparator());
    }
    return comparator;
  }

  /**
   * Overridden to avoid unnecessary cast.
   * 
   * @return This condition for the fluid API.
   */
  @Override
  public final ExpressionBetween<T> NOT() {
    setNegate(!isNegate());
    return this;
  }

  @Override
  public String toString() {
    return isNegate() ? NOT + text() + StringConstant.RIGHT_PARENTHESIS : text();
  }

  private final String text() {
    if (symmetric) {
      return StringConstant.LEFT_PARENTHESIS + constructToString(operand, lowerBound, upperBound)
          + StringConstant.RIGHT_PARENTHESIS + StringConstant.SPACE + BooleanOperator.OR
          + StringConstant.SPACE + StringConstant.LEFT_PARENTHESIS
          + constructToString(operand, upperBound, lowerBound) + StringConstant.RIGHT_PARENTHESIS;
    }
    return constructToString(operand, lowerBound, upperBound);
  }

  private final String constructToString(Operand<T> op, Operand<T> lower, Operand<T> upper) {
    return op.toString() + StringConstant.SPACE + BETWEEN + StringConstant.SPACE + lower
        + StringConstant.SPACE + BooleanOperator.AND + StringConstant.SPACE + upper;
  }

}
