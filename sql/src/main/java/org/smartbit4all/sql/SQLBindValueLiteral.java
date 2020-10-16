package org.smartbit4all.sql;

import org.smartbit4all.domain.meta.OperandLiteral;

/**
 * The bind value literal contains the bind position and the literal also to be able to bind for the
 * statement.
 * 
 * @author Peter Boros
 */
public class SQLBindValueLiteral {

  /**
   * Bind position inside the SQL statement.
   */
  int position;

  /**
   * The literal to access the value while binding.
   */
  OperandLiteral<?> literal;

  /**
   * Constructs a new bind value.
   * 
   * @param position
   * @param literal
   */
  SQLBindValueLiteral(int position, OperandLiteral<?> literal) {
    super();
    this.position = position;
    this.literal = literal;
  }

}
