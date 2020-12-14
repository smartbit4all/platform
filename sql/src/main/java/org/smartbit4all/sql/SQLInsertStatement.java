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
import org.smartbit4all.domain.meta.PropertyOwned;

/**
 * The insert statement with every data for rendering and binding it.
 * 
 * @author Peter Boros
 */
public final class SQLInsertStatement implements SQLStatementNode {

  /**
   * The table for the statement.
   */
  SQLTableNode table;

  /**
   * The columns to be set during the insert.
   */
  private SQLInsertColumns columns = new SQLInsertColumns();

  /**
   * The values in the same order then in the columns.
   */
  private SQLInsertValues values = new SQLInsertValues();

  /**
   * Constructs a new insert statement.
   * 
   * @param table The table for the insert statement.
   */
  public SQLInsertStatement(SQLTableNode table) {
    this.table = table;
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    values.bind(b, stmt);
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.insertInto(table);
    columns.render(b);
    values.render(b);
  }

  public final SQLBindValue addColumn(String columnName, PropertyOwned<?> propertyColumn) {
    columns.addColumn(columnName);
    SQLBindValue result = new SQLBindValue(propertyColumn);
    values.nodes.add(result);
    return result;
  }

}
