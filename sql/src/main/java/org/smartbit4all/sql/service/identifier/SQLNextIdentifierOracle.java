package org.smartbit4all.sql.service.identifier;

import java.sql.SQLException;
import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.service.identifier.NextIdentifier;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLNextIdentifierOracle extends SB4FunctionImpl<String, Long> implements NextIdentifier {

  protected JdbcTemplate jdbcTemplate;

  public SQLNextIdentifierOracle(JdbcTemplate jdbcTemplate) {
    super();
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void execute() throws SQLException {
    output = jdbcTemplate.queryForObject("select " + input + ".nextval from dual", Long.class);
  }

}
