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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.service.identifier.NextIdentifier;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLNextIdentifierH2 extends SB4FunctionImpl<String, Long>
    implements NextIdentifier {

  private static final Logger log = LoggerFactory.getLogger(SQLNextIdentifierH2.class);

  protected JdbcTemplate jdbcTemplate;

  private SQLIdentifierServiceH2 service;

  public SQLNextIdentifierH2(JdbcTemplate jdbcTemplate, SQLIdentifierServiceH2 service) {
    super();
    this.jdbcTemplate = jdbcTemplate;
    this.service = service;
  }

  @Override
  public void execute() throws SQLException {
    try {
      nextVal();
    } catch (Exception e) {
      // Try to create the sequence
      log.warn("Unable to find " + input + " sequence try to create it.", e);
      try {
        jdbcTemplate.execute("create sequence " + input);
      } catch (Exception e1) {
        log.error("Unable to create " + input + " sequence.", e1);
      }
      nextVal();
    }
  }

  private final void nextVal() {
    output = jdbcTemplate.queryForObject("select " + input + ".nextval from dual", Long.class);
    service.incrementSequence(input);
  }

}
