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
package org.smartbit4all.sql.service.modify;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinition.TableDefinition;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.service.CrudApis;
import org.smartbit4all.domain.service.modify.CreateInput;
import org.smartbit4all.domain.service.modify.CreateOutput;
import org.smartbit4all.sql.SQLBindValue;
import org.smartbit4all.sql.SQLInsertStatement;
import org.smartbit4all.sql.SQLStatementBuilder;
import org.smartbit4all.sql.SQLStatementBuilderIF;
import org.smartbit4all.sql.SQLTableNode;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * The create execution is an object containing all the data and objects for the execution.
 *
 * @author Peter Boros
 *
 * @param <E>
 */
public class SQLCreateExecution<E extends EntityDefinition> {

  private static final Logger log = LoggerFactory.getLogger(SQLCreateExecution.class);

  /**
   * The input object.
   */
  private CreateInput<E> input;

  /**
   * The output of the execution.
   */
  private CreateOutput output;

  /**
   * The JDBC connection accessor.
   */
  private JdbcTemplate jdbcTemplate;

  /**
   * The insert statement.
   */
  private SQLInsertStatement insert;

  /**
   * This is the number of records that are executed at the same time using the
   * {@link PreparedStatement#addBatch()}. Default batch size is defined in SQLBatchUtils. If set to
   * 1, batch execution is disabled.
   */
  private int batchExecutionSize = SQLBatchUtils.DEFAULT_BATCH_SIZE;

  private SQLDBParameter sqlDBParameter;

  public SQLCreateExecution(JdbcTemplate jdbcTemplate, CreateInput<E> input,
      SQLDBParameter sqlDBParameter) {
    this.jdbcTemplate = jdbcTemplate;
    this.input = input;
    this.sqlDBParameter = sqlDBParameter;
  }

  /**
   * Sets the batch execution size.
   * 
   * @param batchSize The number of operations to include in a single batch. Set to 1 to disable
   *        batch operations.
   * @return This execution instance for chaining.
   */
  public SQLCreateExecution<E> setBatchExecutionSize(int batchSize) {
    this.batchExecutionSize = SQLBatchUtils.validateBatchSize(batchSize);
    return this;
  }

  public CreateOutput execute() {
    String schema = CrudApis.getCrudApi().getSchema(input.getEntityDefinition());

    SQLStatementBuilderIF builder = new SQLStatementBuilder(sqlDBParameter);
    TableDefinition table = input.getEntityDefinition().tableDefinition();
    SQLTableNode tableNode = new SQLTableNode(schema, table.getName());
    insert = new SQLInsertStatement(tableNode);

    List<SQLBindValue> values = new ArrayList<>();
    for (PropertyOwned<?> property : input.properties()) {
      values.add(insert.addColumn(builder.getColumn(property.getDbExpression()), property));
    }
    List<SQLBindValue> identifiers = new ArrayList<>();
    for (PropertyOwned<?> property : input.identifiedBy()) {
      identifiers.add(insert.addColumn(builder.getColumn(property.getDbExpression()), property));
    }
    insert.render(builder);

    // Execute the before statements if there are any
    for (Entry<String, StringBuilder> entry : builder.beforeStatements().entrySet()) {
      String beforeStatement = entry.getValue().toString();
      if (beforeStatement != null) {
        jdbcTemplate.execute(beforeStatement);
      }
    }

    input.start();

    // Handle single row operations (no batching)
    if (input.size() == 1 ||
        !SQLBatchUtils.useBatchProcessing(batchExecutionSize)) {
      int totalCount = 0;
      for (int row = 0; row < input.size(); row++) {
        SQLPreparedStatementCreator psc = new SQLPreparedStatementCreator(builder, insert);
        setBindValues(values, identifiers, row);
        totalCount += jdbcTemplate.update(psc);
      }
      return new CreateOutput(totalCount);
    }

    // Handle batch operations
    int totalCount = 0;
    int offset = 0;
    int remaining = input.size();
    while (remaining > 0) {
      int currentBatchSize = Math.min(batchExecutionSize, remaining);
      int count = executeBatch(builder, values, identifiers, offset, currentBatchSize);
      if (count != currentBatchSize) {
        log.warn("count != currentBatchSize! {} != {}", count, currentBatchSize);
      }
      offset += currentBatchSize;
      remaining -= currentBatchSize;
      totalCount += count;

    }
    return new CreateOutput(totalCount);
  }

  private void setBindValues(List<SQLBindValue> values,
      List<SQLBindValue> identifiers, int row) {
    for (int i = 0; i < values.size(); i++) {
      SQLBindValue bindValue = values.get(i);
      bindValue.setValue(input.getValue(row, i));
    }
    for (int i = 0; i < identifiers.size(); i++) {
      SQLBindValue bindValue = identifiers.get(i);
      bindValue.setValue(input.getIdValue(row, i));
    }
  }

  private int executeBatch(SQLStatementBuilderIF builder, List<SQLBindValue> values,
      List<SQLBindValue> identifiers, int offset, int batchSize) {

    return SQLBatchUtils.executeBatch(jdbcTemplate, builder.getStatement(),
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws java.sql.SQLException {
            setBindValues(values, identifiers, offset + i);
            insert.bind(builder, ps);
          }

          @Override
          public int getBatchSize() {
            return batchSize;
          }
        });
  }

}
