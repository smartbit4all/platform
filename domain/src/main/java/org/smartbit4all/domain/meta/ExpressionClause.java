/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * The clause is a section of {@link Expression}s with the same logical operator (AND, OR) among
 * them. If we have an expression tree built up from {@link ExpressionBooleFormula} and all of them
 * are AND then it's equivalent with a AND clause containing all the conditions from the tree.
 * 
 * It can be used as a list of {@link Expression} instead of having a List.
 * 
 * @author Peter Boros
 */
public class ExpressionClause extends Expression {

  /**
   * The operator of the boole clause.
   */
  protected BooleanOperator operator;

  /**
   * The expressions of the clause
   */
  protected final List<Expression> expressions = new ArrayList<>();

  /**
   * Overridden to avoid unnecessary cast.
   * 
   * @return This condition for the fluid API.
   */
  @Override
  public Expression NOT() {
    setNegate(!isNegate());
    return this;
  }

  /**
   * Creates a new clause waiting for expressions to add.
   * 
   * @param operator
   */
  ExpressionClause(BooleanOperator operator) {
    super();
    this.operator = operator;
  }

  /**
   * Creates a new clause waiting for expressions to add.
   * 
   * @param operator
   */
  ExpressionClause(BooleanOperator operator, Expression first, Expression second) {
    super();
    this.operator = operator;
    expressions.add(first);
    expressions.add(second);
  }

  @Override
  public void accept(ExpressionVisitor visitor) {
    // If we don't have any expression then skip this.
    if (expressions.isEmpty()) {
      return;
    }
    // If we have only one expression then we delegate simply.
    if (expressions.size() == 1) {
      expressions.get(0).accept(visitor);
      return;
    }
    // If we have more expression then we simulate that we have boolean formulas cascaded into each
    // other.
    boolean first = true;
    for (Expression expression : expressions) {
      if (!first) {
        visitor.visitClause(this);
      }
      first = false;
      expression.accept(visitor);
    }
  }

  @Override
  public boolean evaluate() {
    return isNegate() ? !myEvaluate() : myEvaluate();
  }

  private final boolean myEvaluate() {
    switch (operator) {
      case AND:
        return evaluateAnd();

      case OR:
        return evaluateOr();

      case XOR:
        return evaluateXor();

      default:
        return false;
    }
  }

  private final boolean evaluateXor() {
    // If we don't have any condition then it's true
    if (expressions.isEmpty()) {
      return true;
    }
    // If we have exactly one true condition.
    boolean alreadyHave = false;
    for (Expression expression : expressions) {
      if (expression.evaluate()) {
        if (alreadyHave) {
          // It's not the first true. --> We are false
          return false;
        } else {
          alreadyHave = true;
        }
      }
    }
    return alreadyHave;
  }

  private final boolean evaluateOr() {
    // If we don't have any condition then it's true
    if (expressions.isEmpty()) {
      return true;
    }
    for (Expression expression : expressions) {
      if (expression.evaluate()) {
        return true;
      }
    }
    return false;
  }

  private final boolean evaluateAnd() {
    for (Expression expression : expressions) {
      if (!expression.evaluate()) {
        return false;
      }
    }
    // We are true even if we have no condition or we didn't find false.
    return true;
  }

  public final BooleanOperator getOperator() {
    return operator;
  }

  public final void setOperator(BooleanOperator operator) {
    this.operator = operator;
  }

  /**
   * The expressions of the given clause.
   * 
   * @return
   */
  public List<Expression> expressions() {
    return expressions;
  }
  
  /**
   * Adds an expression to the clause.
   * 
   * @param expression The expression to add.
   * @return The clause itself.
   */
  public ExpressionClause add(Expression expression) {
    if(expression instanceof ExpressionClause) {
      ExpressionClause clauseToAdd = (ExpressionClause) expression;
      if(this.operator == clauseToAdd.operator) {
        this.expressions.addAll(clauseToAdd.expressions);
      } else {
        expressions.add(expression);
      }
    } else {
      expressions.add(expression);
    }
    return this;
  }

}
