package org.smartbit4all.sql.service.query;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.query.QueryExecutionApi;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLQueryExecutionApi implements QueryExecutionApi {

  /**
   * The JDBC connection accessor.
   */
  JdbcTemplate jdbcTemplate;

  public SQLQueryExecutionApi(JdbcTemplate jdbcTemplate) {
    super();
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public <E extends EntityDefinition> QueryOutput execute(QueryInput queryInput) throws Exception {
    SQLQueryExecution queryExecution = new SQLQueryExecution(jdbcTemplate, queryInput);
    queryExecution.execute();
    return queryExecution.queryOutput;
  }

}
