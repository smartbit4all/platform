/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Group by statement for SqlComputed columns. The main difference between the
 * {@link SQLGroupByColumn} and this is it refers to the alias of the computed column because it's
 * present is guaranteed.
 * 
 * @author bhorvath
 */
public class SQLGroupByComputedColumn extends SQLGroupByColumn {

  /**
   * Constructs a new group by column.
   * 
   */
  public SQLGroupByComputedColumn(String computedColAlias) {
    super(null, computedColAlias);
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(columnName);
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // There is nothing to bind.
  }

}
