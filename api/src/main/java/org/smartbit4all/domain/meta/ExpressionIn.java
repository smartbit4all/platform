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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The IN expression tests the {@link #operand} against the {@link #values} set. If it's found in
 * the set then the result is true. This expression doesn't accept null in the values. Use
 * {@link ExpressionIsNull} instead.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public final class ExpressionIn<T> extends Expression {

  public static final String NOT_IN = "NOT IN";

  public static final String IN = "IN";

  /**
   * The operand that is checked against the list of values.
   */
  private Operand<T> operand;

  /**
   * The set of values.
   */
  private Set<T> values;

  /**
   * Creates a new IN expression.
   * 
   * @param operand The operand that will be tested against the values.
   * @param values The collection of value. The expression will store these values in a set so every
   *        value will appear only once.
   */
  public ExpressionIn(Operand<T> operand, Collection<T> values) {
    super();
    this.operand = operand;
    this.values = new HashSet<>(values == null ? Collections.emptySet() : values);
  }

  @Override
  public ExpressionIn<T> copy() {
    return new ExpressionIn<>(this);
  }

  /**
   * The copy constructor.
   * 
   * @param exp
   */
  public ExpressionIn(ExpressionIn<T> exp) {
    this.operand = exp.operand;
    this.values = new HashSet<>(exp.values == null ? Collections.emptySet() : exp.values);
    this.setNegate(exp.isNegate());
  }


  @Override
  public boolean evaluate() {
    return isNegate() ? !myEvaluate() : myEvaluate();
  }

  private boolean myEvaluate() {
    return operand == null ? false : getValues().contains(getOpValue());
  }

  private final T getOpValue() {
    if (operand == null) {
      return null;
    }
    if (operand instanceof OperandProperty) {
      return ((OperandProperty<T>) operand).applyFunction(operand.value());
    }
    return operand.value();
  }

  private final Set<T> getValues() {
    if (operand instanceof OperandProperty) {
      return values.stream()
          .map(v -> ((OperandProperty<T>) operand).applyFunction(v))
          .collect(Collectors.toSet());
    }
    return values;
  }

  @Override
  public void accept(ExpressionVisitor visitor) {
    visitor.visitIn(this);
  }

  /**
   * Overridden to avoid unnecessary cast.
   * 
   * @return This condition for the fluid API.
   */
  @Override
  public final ExpressionIn<T> NOT() {
    setNegate(!isNegate());
    return this;
  }

  /**
   * The {@link #operand} of the in expression.
   * 
   * @return
   */
  public final Operand<T> getOperand() {
    return operand;
  }

  public final Set<T> values() {
    return values;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(operand.toString());
    builder.append(StringConstant.SPACE);
    builder.append(isNegate() ? NOT_IN : IN);
    builder.append(StringConstant.SPACE);
    builder.append(StringConstant.LEFT_CURLY);
    int i = 0;
    for (T t : values) {
      if (i > 0) {
        builder.append(StringConstant.COMMA_SPACE);
      }
      builder.append(OperandLiteral.text(t));
      i++;
    }
    builder.append(StringConstant.RIGHT_CURLY);
    return builder.toString();
  }

}
