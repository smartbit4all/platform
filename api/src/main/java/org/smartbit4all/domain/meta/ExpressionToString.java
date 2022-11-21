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

import org.smartbit4all.core.utility.StringConstant;

/**
 * A toString helper for the {@link Expression} hierarchy. It produce the textual representation of
 * an expression tree in a readable sequential format.
 * 
 * @author Peter Boros
 */
final class ExpressionToString extends ExpressionVisitor {

  /**
   * This builder of the final textual representation.
   */
  StringBuilder builder = new StringBuilder();

  private ExpressionToString() {
    super();
  }

  private final String appendSpace() {
    if (builder.length() > 0) {
      return StringConstant.SPACE;
    }
    return StringConstant.EMPTY;
  }


  @Override
  public void visitClause(ExpressionClause expression) {
    builder.append(appendSpace() + expression.getOperator());
  }

  @Override
  public void visitBracketPost(ExpressionBracket expression) {
    builder.append(StringConstant.RIGHT_PARENTHESIS);
  }

  @Override
  public void visitBracketPre(ExpressionBracket expression) {
    builder.append(appendSpace() + (expression.isNegate() ? "NOT " : StringConstant.EMPTY)
        + StringConstant.LEFT_PARENTHESIS);
  }

  @Override
  public <T> void visit2Operand(Expression2Operand<T> expression) {
    builder.append(appendSpace() + expression.toString());
  }

  @Override
  public void visitBetween(ExpressionBetween<?> expression) {
    builder.append(appendSpace() + expression.toString());
  }

  @Override
  public <T> void visitIn(ExpressionIn<T> expression) {
    builder.append(appendSpace() + expression.toString());
  }

  @Override
  public void visitIsNull(ExpressionIsNull expression) {
    builder.append(appendSpace() + expression);
  }

  @Override
  public void visitBoolean(ExpressionBoolean expression) {
    builder.append(appendSpace() + expression);
  }

  @Override
  public void visitInDataSet(ExpressionInDataSet expressionInDataSet) {
    builder.append(appendSpace() + expressionInDataSet);
  }
  
  @Override
  public void visitExists(ExpressionExists expression) {
    builder.append(appendSpace() + expression);
  }

  /**
   * Create the textual representation for the given expression using the {@link ExpressionVisitor}
   * mechanism.
   * 
   * @param exp
   * @return
   */
  public static String toString(Expression exp) {
    ExpressionToString toString = new ExpressionToString();
    exp.accept(toString);
    return toString.builder.toString();
  }

}
