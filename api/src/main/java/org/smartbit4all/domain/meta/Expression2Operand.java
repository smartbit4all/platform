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
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The two operand expression for the equal, less than, less or equal, greater than, greater or
 * equal and like.
 * 
 * TODO Modifier like case sensitive.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public final class Expression2Operand<T> extends Expression {

  public static enum Operator {
    EQ, LT, LE, GT, GE, LIKE;

    public String text() {
      switch (this) {
        case EQ:
          return StringConstant.EQUAL;
        case LT:
          return StringConstant.LESS;
        case LE:
          return StringConstant.LESSOREQUAL;
        case GT:
          return StringConstant.GREATER;
        case GE:
          return StringConstant.GREATEROREQUAL;
        case LIKE:
          return name();
      }
      return StringConstant.EMPTY;
    }

  }

  private Operator operator;

  private OperandProperty<T> op;

  private OperandLiteral<T> literal;

  /**
   * The comparator to compare the values of the two operand.
   */
  private Comparator<T> comparator = null;

  /**
   * Creates a new boolean expression with two operand infix style.
   * 
   * @param op Operand
   * @param operator Operator
   * @param literal The literal value
   */
  public Expression2Operand(OperandProperty<T> op, Operator operator, OperandLiteral<T> literal) {
    super();
    this.op = op;
    this.operator = operator;
    this.literal = literal;
  }


  @Override
  public Expression copy() {
    return new Expression2Operand<>(this);
  }

  /**
   * The copy constructor.
   * 
   * @param exp
   */
  public Expression2Operand(Expression2Operand<T> exp) {
    this.op = exp.op;
    this.operator = exp.operator;
    this.literal = exp.literal;
    this.setNegate(exp.isNegate());
  }

  public final Operator operator() {
    return operator;
  }

  public final void setOperator(Operator operator) {
    this.operator = operator;
  }

  @Override
  public boolean evaluate() {
    return isNegate() ? !myEvaluate() : myEvaluate();
  }

  /**
   * The evaluation without assuming the negate.
   * 
   * @return
   */
  private boolean myEvaluate() {
    switch (operator) {
      case EQ:
        return Objects.equals(op.value(), literal.value());
      case GE:
        return Objects.compare(op.value(), literal.value(), getComparator()) >= 0;
      case GT:
        return Objects.compare(op.value(), literal.value(), getComparator()) > 0;
      case LE:
        return Objects.compare(op.value(), literal.value(), getComparator()) <= 0;
      case LT:
        return Objects.compare(op.value(), literal.value(), getComparator()) < 0;
      case LIKE:
        return like();
      default:
        return false;
    }
  }

  private final boolean like() {
    boolean opNull = op == null || op.value() == null;
    boolean literalNull = literal == null || literal.value() == null;
    if (opNull && literalNull) {
      // TODO The two null operand mean true?
      return true;
    }
    if (opNull) {
      return false;
    } else if (literalNull) {
      return true;
    }

    String opString = op.value().toString();
    String literalString = literal.value().toString();
    // TODO Collation handling with redundant column in the data table.
    String zeroOrMore = "%";
    String single = "_";
    if (literalString.indexOf(zeroOrMore) != -1 || literalString.indexOf(single) != -1) {
      // The classic SQL regular expressions are replaced with java regular expression. Surrounded
      // with \Q, \E to avoid changing the rest of the expression.
      literalString = Pattern.quote(literalString);
      literalString = literalString.replaceAll(zeroOrMore, "\\\\E.*\\\\Q");
      literalString = literalString.replaceAll(single, "\\\\E.\\\\Q");
      return opString.matches(literalString);
    } else {
      return opString.equals(literalString);
    }
  }


  private final Comparator<T> getComparator() {
    if (comparator == null) {
      comparator = Comparator.nullsFirst(op.property.getComparator());
    }
    return comparator;
  }

  @Override
  public void accept(ExpressionVisitor visitor) {
    visitor.visit2Operand(this);
  }

  /**
   * Overridden to avoid unnecessary cast.
   * 
   * @return This condition for the fluid API.
   */
  @Override
  public final Expression2Operand<T> NOT() {
    setNegate(!isNegate());
    return this;
  }

  public final OperandProperty<T> getOp() {
    return op;
  }

  public final void setOp(OperandProperty<T> op) {
    this.op = op;
  }

  public final OperandLiteral<T> getLiteral() {
    return literal;
  }

  public final void setLiteral(OperandLiteral<T> literal) {
    this.literal = literal;
  }

  @Override
  public String toString() {
    return isNegate() ? NOT + text() + StringConstant.RIGHT_PARENTHESIS : text();
  }

  private String text() {
    return op.toString() + StringConstant.SPACE + operator.text() + StringConstant.SPACE
        + literal.toString();
  }

  public void setOperandProcessor(UnaryOperator<T> operandProcessor) {
    op.setProcessor(operandProcessor);
  }

}
