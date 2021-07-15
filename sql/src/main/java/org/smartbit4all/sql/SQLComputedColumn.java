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

import java.util.ArrayList;
import org.smartbit4all.core.utility.StringConstant;

public class SQLComputedColumn extends SQLSelectColumn {

  private ArrayList<SQLSelectColumn> requiredColumns;
  
  public SQLComputedColumn(SQLSelectFromNode from, String columnName, String alias, ArrayList<SQLSelectColumn> requiredColumns) {
    super(from, columnName, alias);
    this.requiredColumns = requiredColumns;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    if(columnName.startsWith(StringConstant.LEFT_PARENTHESIS)) {
      b.append(columnName);
    } else {
      b.append(StringConstant.LEFT_PARENTHESIS);
      b.append(columnName);
      b.append(StringConstant.RIGHT_PARENTHESIS);
    }
    b.append(StringConstant.SPACE);
    b.append(alias);
  }
  
  public ArrayList<SQLSelectColumn> getRequiredColumns() {
    return requiredColumns;
  }
  
  @Override
  public String getNameWithFrom() {
    /* in case of a computed column it is wrong to add the from to any column name, so we return the
     * bare columnName*/
    return columnName;
  }
}
