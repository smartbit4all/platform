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

/**
 * The select from section with a table and an alias. These nodes are depending on each other with
 * joins and they form a directed acyclic graph. So we will have a root node as a starting point of
 * the traversal. The SQL itself is the result of a depth first search algorithm where the traversal
 * of a join edge means a join to the already joined structure.
 * 
 * @author Peter Boros
 */
public class SQLSelectFromTableNode extends SQLSelectFromNode {

  /**
   * The table in the from section of the select.
   */
  SQLTableNode table;

  /**
   * Constructs a new table node with the table and the alias we have.
   * 
   * @param table
   * @param alias
   */
  public SQLSelectFromTableNode(SQLTableNode table, String alias) {
    super(alias);
    this.table = table;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(table);
    b.append(SQLConstant.SEGMENTSEPARATOR);
    b.append(alias);
  }

}
