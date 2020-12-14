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
 * The update statement with every data for rendering and binding it.
 * 
 * @author Peter Boros
 */
public final class SQLUpdateStatement implements SQLStatementNode {

  /**
   * The table for the statement.
   */
  SQLTableNode table;

  /**
   * The columns to be set during the insert.
   */
  private SQLUpdateValues values = new SQLUpdateValues();

  /**
   * This is the where for the update statement.
   */
  private SQLWhere where = null;

  /**
   * Constructs a new insert statement.
   * 
   * @param table The table for the insert statement.
   */
  public SQLUpdateStatement(SQLTableNode table) {
    this.table = table;
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    values.bind(b, stmt);
    where.bind(b, stmt);
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.update(table);
    values.render(b);

    b.preProcessWhere();
    where.render(b);
  }

  public final SQLBindValue addColumn(PropertyOwned<?> propertyColumn) {
    SQLBindValue result = new SQLBindValue(propertyColumn);
    values.nodes.add(result);
    return result;
  }

  public SQLWhere getWhere() {
    return where;
  }

  public void setWhere(SQLWhere where) {
    this.where = where;
  }

}
