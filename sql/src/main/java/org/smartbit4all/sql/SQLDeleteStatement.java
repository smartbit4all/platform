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
 * The delete SQL statement.
 * 
 * @author Peter Boros
 */
public class SQLDeleteStatement implements SQLStatementNode {


  /**
   * The table for the statement.
   */
  SQLTableNode table;

  /**
   * This is the where for the delete statement.
   */
  private SQLWhere where = null;

  /**
   * Constructs a delete statement.
   * 
   * @param table The table of the statement.
   */
  public SQLDeleteStatement(SQLTableNode table) {
    this.table = table;
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    if (where != null) {
      where.bind(b, stmt);
    }
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.deleteFrom(table);
    if (where != null) {
      b.preProcessWhere();
      where.render(b);
    }
  }

  public SQLWhere getWhere() {
    return where;
  }

  public void setWhere(SQLWhere where) {
    this.where = where;
  }

}
