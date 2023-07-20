package org.smartbit4all.sql.service.identifier;

import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.service.identifier.NextIdentifier;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLNextIdentifierPg extends SB4FunctionImpl<String, Long> implements NextIdentifier {

  private static final String TEMPLATE = "select nextval('%s')";

  protected JdbcTemplate jdbcTemplate;

  public SQLNextIdentifierPg(JdbcTemplate jdbcTemplate) {
    super();
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void execute() throws Exception {
    output = jdbcTemplate.queryForObject(String.format(TEMPLATE, input), Long.class);
  }

}
