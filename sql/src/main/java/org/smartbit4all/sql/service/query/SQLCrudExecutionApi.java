package org.smartbit4all.sql.service.query;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.modify.CreateInput;
import org.smartbit4all.domain.service.modify.CreateOutput;
import org.smartbit4all.domain.service.modify.DeleteInput;
import org.smartbit4all.domain.service.modify.DeleteOutput;
import org.smartbit4all.domain.service.modify.UpdateInput;
import org.smartbit4all.domain.service.modify.UpdateOutput;
import org.smartbit4all.domain.service.query.CrudExecutionApi;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.smartbit4all.sql.service.modify.SQLCreateExecution;
import org.smartbit4all.sql.service.modify.SQLDeleteExecution;
import org.smartbit4all.sql.service.modify.SQLUpdateExecution;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLCrudExecutionApi implements CrudExecutionApi {

  /**
   * The JDBC connection accessor.
   */
  JdbcTemplate jdbcTemplate;

  private String schema = null;

  protected SQLDBParameter sqlDBParameter;

  public SQLCrudExecutionApi(JdbcTemplate jdbcTemplate) {
    super();
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public <E extends EntityDefinition> QueryOutput executeQuery(QueryInput queryInput) {
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

  @Override
  public <E extends EntityDefinition> CreateOutput executeCreate(CreateInput<E> input) {
    return new SQLCreateExecution<>(jdbcTemplate, input).execute();
  }

  @Override
  public <E extends EntityDefinition> UpdateOutput executeUpdate(UpdateInput<E> input) {
    return new SQLUpdateExecution<>(jdbcTemplate, input).execute();
  }

  @Override
  public <E extends EntityDefinition> DeleteOutput executeDelete(DeleteInput<E> input) {
    return new SQLDeleteExecution<>(jdbcTemplate, input).execute();
  }

}
