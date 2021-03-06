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

import java.lang.ref.WeakReference;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * An order by column is an item in the select.
 * 
 * @author Peter Boros
 */
public class SQLOrderByColumn implements SQLStatementNode {

  /**
   * Every column belongs to a from section. It defines the alias of the column. It's a
   * {@link WeakReference} to help gc mechanism.
   */
  WeakReference<SQLSelectFromNode> from;

  /**
   * The "name" of the column.
   */
  String columnName;

  /**
   * Defines if it's ascending or descending.
   */
  boolean asc;

  /**
   * Defines if we want the nulls first or at the last.
   */
  boolean nullsFirst = false;

  /**
   * Constructs a new group by column.
   * 
   * @param from
   * @param columnName The column name.
   */
  public SQLOrderByColumn(SQLSelectFromNode from, String columnName, boolean asc,
      boolean nullsFirst) {
    super();
    this.from = new WeakReference<>(from);
    this.columnName = columnName;
    this.asc = asc;
    this.nullsFirst = nullsFirst;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(this);
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // There is nothing to bind.
  }

}
