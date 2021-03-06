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

/**
 * The abstract condition node in a condition expression. The condition tree is implemented as a
 * modification of expression tree. It can express a condition that can be evaluated if the data is
 * bound. The condition are based on {@link Property} so we can use the properties to produce a
 * condition. At the same time the condition is a builder that allows building a complex condition
 * by using the AND, OR, BRACKET operations. It provides a fluent API where the condition itself is
 * the builder also.
 * 
 * The expression tree consists of the conditions that can be structural or exact. The structural
 * ones are bracket, and, or the exact ones are equal, less than, greater than, in and so on. The
 * bracket and the exact conditions can be negated.
 * 
 * <pre>
 *                 -- Exp2 ( Property(A) = Literal(5) )
 *                 |
 *       -- AND -- +            
 *       |         |
 * OR -- +         -- Exp2 ( Property(B) = Literal("alma") )
 *       |
 *       |         -- Exp2 ( Property(C) = Literal( {@link SimpleDateFormat#parse("2020.03.15")}) )
 *       |         |
 *       -- AND -- +              -- Exp1 ( Property(B) ISNULL )
 *                 |              |
 *                 -- () -- OR -- +
 *                                |
 *                                -- ExpN ( Property(D) IN ("a", "b", "c") )
 * </pre>
 * 
 * The expression tree can be process with the visitor pattern as described in the Gang Of Four
 * published visitor pattern.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Binary_expression_tree">Expression tree</a>
 * @see <a href="https://en.wikipedia.org/wiki/Builder_pattern">Builder pattern</a>
 * @see <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor pattern</a>
 * 
 * @author Peter Boros
 *
 */
public abstract class Expression {

  /**
   * If the logical result of the expression is negative true = false false = true
   */
  protected boolean negate = false;

  protected static final String NOT = "NOT (";

  /**
   * Creates a boolean condition with this and the parameter condition.
   * 
   * @param expression
   * @return An {@link ExpressionClause}.
   */
  public final ExpressionClause AND(Expression expression) {
    return new ExpressionClause(BooleanOperator.AND, this, expression);
  }

  /**
   * Creates a boolean condition with this or the parameter condition.
   * 
   * @param expression
   * @return An {@link ExpressionClause}.
   */
  public final ExpressionClause OR(Expression expression) {
    return new ExpressionClause(BooleanOperator.OR, this, expression);
  }

  /**
   * Creates a boolean condition with this exclusive or the parameter condition.
   * 
   * @param expression
   * @return An {@link ExpressionClause}.
   */
  public final ExpressionClause XOR(Expression expression) {
    return new ExpressionClause(BooleanOperator.XOR, this, expression);
  }

  public final ExpressionBracket BRACKET() {
    return new ExpressionBracket(this);
  }

  /**
   * Negate the current condition. The subsequent calls are changing the not true and false again
   * and again.
   * 
   * @return This condition for the fluid API.
   */
  public abstract Expression NOT();

  /**
   * Creates an always true expression.
   * 
   * @return
   */
  public static final ExpressionBoolean TRUE() {
    return new ExpressionBoolean(true);
  }

  /**
   * Creates an always false expression.
   * 
   * @return
   */
  public static final ExpressionBoolean FALSE() {
    return new ExpressionBoolean(false);
  }

  /**
   * As it's described by the visitor pattern this accept a traversal for the expression structure.
   * The visitor object must implement the API for visiting all kind of expressions.
   * 
   * @param visitor
   * @return
   */
  public abstract void accept(ExpressionVisitor visitor);

  /**
   * Evaluate the whole expression tree to decide if it's true or false.
   * 
   * @return
   */
  public abstract boolean evaluate();

  /**
   * If the logical result of the expression is negative true = false false = true
   * 
   * @return
   */
  public final boolean isNegate() {
    return negate;
  }

  /**
   * Set if the logical result of the expression is negative true = false false = true
   * 
   * @param negate
   */
  public final void setNegate(boolean negate) {
    this.negate = negate;
  }
  
  @Override
  public String toString() {
    return ExpressionToString.toString(this);
  }

  /**
   * Creates a new AND clause.
   * @return an empty {@link ExpressionClause} with {@link BooleanOperator#AND AND} operator.
   */
  public static final ExpressionClause createAndClause() {
    return new ExpressionClause(BooleanOperator.AND);
  }
  
  /**
   * Creates a new OR clause.
   * @return an empty {@link ExpressionClause} with {@link BooleanOperator#OR OR} operator.
   */
  public static final ExpressionClause createOrClause() {
    return new ExpressionClause(BooleanOperator.OR);
  }
  
  /**
   * Creates a new XOR clause.
   * @return an empty {@link ExpressionClause} with {@link BooleanOperator#XOR XOR} operator.
   */
  public static final ExpressionClause createXorClause() {
    return new ExpressionClause(BooleanOperator.XOR);
  }
  
}
