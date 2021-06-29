package org.smartbit4all.sql.service.query;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.query.Query;
import org.smartbit4all.domain.service.query.QueryExecutionApi;
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

  @SuppressWarnings("unchecked")
  @Override
  public <E extends EntityDefinition> QueryOutput<E> execute(Query<E> queryRequest) throws Exception {
    SQLQueryExecution queryExecution = new SQLQueryExecution(jdbcTemplate, queryRequest);
    queryExecution.execute();
    return (QueryOutput<E>) queryExecution.query.output();
  }

}
