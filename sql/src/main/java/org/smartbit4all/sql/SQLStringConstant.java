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
 * An active part of the statement that is not bound and means only a fix string. It will be added
 * to the statement as is.
 * 
 * @author Peter Boros
 */
public class SQLStringConstant implements SQLCommonValueNode {

  private final String stringConstant;

  public SQLStringConstant(String sqlTag) {
    super();
    this.stringConstant = sqlTag;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(stringConstant);
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // Can be emty there is no need to bind.
  }

}
