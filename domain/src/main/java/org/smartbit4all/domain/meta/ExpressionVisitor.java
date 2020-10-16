package org.smartbit4all.domain.meta;

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

}
