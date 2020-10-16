package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The node is the main building block of the generated SQL statements. The statement itself is a
 * specific version of interpreter pattern and expression templates. This is used to be able to
 * express the SQL statement in an object structure and render the final statement text as an
 * interpretation of this expression.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Interpreter_pattern">Interpreter pattern</a>
 * @see <a href="https://en.wikipedia.org/wiki/Expression_templates">Expression template</a>
 * @author Peter Boros
 *
 */
public interface SQLStatementNode {

  /**
   * The statement structure can be rendered into the builder. The builder contains the specific
   * knowledge of the SQL dialect.
   * 
   * @param b
   */
  public void render(SQLStatementBuilderIF b);

  /**
   * Bind the statement for the JDBC
   * 
   * @param b The builder
   * @param stmt The JDBC statement
   * @throws SQLException
   */
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException;

}
