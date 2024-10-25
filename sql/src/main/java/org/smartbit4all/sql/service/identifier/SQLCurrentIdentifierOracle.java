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
package org.smartbit4all.sql.service.identifier;

import java.sql.SQLException;
import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.service.identifier.CurrentIdentifier;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLCurrentIdentifierOracle extends SB4FunctionImpl<String, Long>
    implements CurrentIdentifier {

  protected JdbcTemplate jdbcTemplate;

  public SQLCurrentIdentifierOracle(JdbcTemplate jdbcTemplate) {
    super();
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void execute() throws SQLException {
    output = jdbcTemplate.queryForObject("select " + input + ".curval from dual", Long.class);
  }

}
