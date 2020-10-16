package org.smartbit4all.sql.service.identifier;

import org.smartbit4all.domain.service.identifier.IdentifierService;
import org.smartbit4all.domain.service.identifier.NextIdentifier;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLIdentifierService implements IdentifierService {

  protected JdbcTemplate jdbcTemplate;

  public SQLIdentifierService(JdbcTemplate jdbcTemplate) {
    super();
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public NextIdentifier next() {
    return new SQLNextIdentifierOracle(jdbcTemplate);
  }

}
