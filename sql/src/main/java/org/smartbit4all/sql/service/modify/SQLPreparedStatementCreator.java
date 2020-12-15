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
package org.smartbit4all.sql.service.modify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.smartbit4all.sql.SQLStatementBuilderIF;
import org.smartbit4all.sql.SQLStatementNode;
import org.springframework.jdbc.core.PreparedStatementCreator;

final class SQLPreparedStatementCreator implements PreparedStatementCreator {
  private final SQLStatementBuilderIF builder;
  PreparedStatement ps = null;
  SQLStatementNode sqlStatement;

  SQLPreparedStatementCreator(SQLStatementBuilderIF builder,
      SQLStatementNode sqlStatement) {
    this.builder = builder;
    this.sqlStatement = sqlStatement;
  }

  @Override
  public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
    if (ps == null) {
      ps = con.prepareStatement(builder.getStatement());
    }
    sqlStatement.bind(builder, ps);
    return ps;
  }
}
