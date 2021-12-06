package org.smartbit4all.sql.service.query;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.query.QueryExecutionApi;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLQueryExecutionApi implements QueryExecutionApi {

  /**
   * The JDBC connection accessor.
   */
  JdbcTemplate jdbcTemplate;

  private String schema = null;

  protected SQLDBParameter sqlDBParameter;

  public SQLQueryExecutionApi(JdbcTemplate jdbcTemplate) {
    super();
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public <E extends EntityDefinition> QueryOutput execute(QueryInput queryInput) throws Exception {
    SQLQueryExecution queryExecution =
        new SQLQueryExecution(jdbcTemplate, queryInput, schema, sqlDBParameter);
    queryExecution.execute();
    return queryExecution.queryOutput;
  }

  @Override
  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public void setSqlDbParameter(SQLDBParameter sqlDBParameter) {
    this.sqlDBParameter = sqlDBParameter;
  }

}
