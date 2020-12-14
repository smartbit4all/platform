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
 *
 * The column list for the insert statement.
 * 
 * @author Peter Boros
 */
public class SQLInsertColumns extends SQLNodeList<SQLStringConstant> {

  /**
   * Add the column name as node to the nodes list.
   * 
   * @param columnName The name of the column.
   */
  public void addColumn(String columnName) {
    nodes.add(new SQLStringConstant(columnName));
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(StringConstant.SPACE);
    if (!nodes.isEmpty()) {
      b.append(StringConstant.LEFT_PARENTHESIS);
      super.render(b);
      b.append(StringConstant.RIGHT_PARENTHESIS);
    }
  }

  /**
   *
   */
  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // Can be empty because there cann't be anything to bind.
  }

}
