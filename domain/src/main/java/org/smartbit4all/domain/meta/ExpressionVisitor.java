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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This visitor is created for the {@link Expression} to be able to expand the process mechanisms
 * without adding new methods to the original expression class. This is commonly used pattern that
 * was published first by the gang of four in the late 90's.
 * 
 * It's a kind of modification because we add more control point during the traversal. We add pre-
 * and post visit functions.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor pattern</a>
 * 
 * @author Peter Boros
 *
 */
public abstract class ExpressionVisitor {

  /**
   * If we enter an {@link ExpressionClause} or an {@link ExpressionBracket} then this will be the
   * parent.
   */
  private ExpressionContainer parent;

  /**
   * Contains the path until we reach the currently visited expression. At the first level of visit
   * we will get an empty list. The first element is the root expression the last is the parent.
   */
  private final List<Expression> expressionStack = new ArrayList<>();

  protected final ExpressionContainer getParent() {
    return parent;
  }

  final ExpressionContainer setParent(ExpressionContainer parent) {
    ExpressionContainer prevParent = this.parent;
    this.parent = parent;
    return prevParent;
  }

  protected List<Expression> getExpressionStack() {
    return expressionStack;
  }

  final void addToStack(Expression exp) {
    expressionStack.add(exp);
  }

  final void removeFromStack() {
    if (!expressionStack.isEmpty()) {
      expressionStack.remove(expressionStack.size() - 1);
    }
  }

  /**
   * Visiting the boole formula itself. Because of the pre order traversal the A part was already
   * visited and B section will be the next.
   * 
   * @param expression
   */
  public void visitClause(ExpressionClause expression) {}

  /**
   * Typed visiting method.
   * 
   * @param expression
   */
  public void visitBracketPost(ExpressionBracket expression) {}

  /**
   * Typed visiting method.
   * 
   * @param expression
   */
  public void visitBracketPre(ExpressionBracket expression) {}

  /**
   * Typed visiting method.
   * 
   * @param expression
   */
  public <T> void visit2Operand(Expression2Operand<T> expression) {}

  /**
   * Typed visiting method.
   * 
   * @param expression
   */
  public void visitBetween(ExpressionBetween<?> expression) {}

  /**
   * Typed visiting method.
   * 
   * @param expression
   */
  public <T> void visitIn(ExpressionIn<T> expression) {}

  /**
   * Typed visiting method.
   * 
   * @param expression
   */
  public void visitIsNull(ExpressionIsNull expression) {}

  /**
   * Typed visiting method.
   * 
   * @param expression
   */
  public void visitBoolean(ExpressionBoolean expression) {}

  /**
   * Typed visiting method.
   * 
   * @param expression
   */
  public void visitExists(ExpressionExists expression) {}

  /**
   * Typed visiting method.
   * 
   * @param expression
   */
  public void visitInDataSet(ExpressionInDataSet expressionInDataSet) {}

}
