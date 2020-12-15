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
import org.smartbit4all.core.utility.StringConstant;

/**
 * The table node encapsulate the table name and the schema handling. We can have tables in every
 * SQL statement but at different positions. It's usually used by the SQL Select statement.
 * 
 * @author Peter Boros
 */
public class SQLTableNode implements SQLStatementNode {

  /**
   * The databases usually defines schemas. They can have different level of support but anyway from
   * the perspective of an SQL select they are namespaces. So if we know this namespace then we have
   * to use it to construct the qualified name of a table.
   */
  String schema;

  /**
   * The name of the table that is used in the SQL statement. Be careful with the case of this
   * String. It must match with the database table name handling. If you need case sensitive table
   * names then use {@link StringConstant#DOUBLE_QUOTE} inside the name!
   */
  String name;

  /**
   * Constructs a table node.
   * 
   * @param schema The database schema.
   * @param name The name of the table.
   */
  public SQLTableNode(String schema, String name) {
    super();
    this.schema = schema;
    this.name = name;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(this);
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // There is no need to bind.
  }

}
