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
 * A select column is an item in the select list. It belongs to a given from node.
 * 
 * @author Peter Boros
 */
public class SQLSelectColumn implements SQLStatementNode {

  /**
   * Every select column belongs to a from section. It defines the alias of the column. It's a
   * {@link WeakReference} to help gc mechanism.
   */
  WeakReference<SQLSelectFromNode> from;

  /**
   * The "name" of the column. It ban be an aggregate function or a column directly.
   */
  String columnName;

  /**
   * The alias of the given column.
   */
  String alias;
  
  private String functionName;


  /**
   * Constructs a new select column.
   * 
   * @param from
   * @param columnName The column name.
   * @param alias The alias for the column in the select. It will be the name of the column in the
   *        JDBC result set.
   */
  public SQLSelectColumn(SQLSelectFromNode from, String columnName, String alias) {
    super();
    this.from = new WeakReference<>(from);
    this.columnName = columnName;
    this.alias = alias;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(this);
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // There is nothing to bind.
  }

  public String getColumnName() {
    return columnName;
  }

  public String getAlias() {
    return alias;
  }

  public SQLSelectFromNode from() {
    return from.get();
  }

  public String getFunctionName() {
    return functionName;
  }

  public void setFunctionName(String functionName) {
    this.functionName = functionName;
  }

}
