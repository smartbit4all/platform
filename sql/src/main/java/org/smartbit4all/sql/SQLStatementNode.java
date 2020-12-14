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
