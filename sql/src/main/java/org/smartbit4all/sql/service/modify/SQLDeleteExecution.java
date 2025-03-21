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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinition.TableDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.service.CrudApis;
import org.smartbit4all.domain.service.modify.DeleteInput;
import org.smartbit4all.domain.service.modify.DeleteOutput;
import org.smartbit4all.sql.SQLDeleteStatement;
import org.smartbit4all.sql.SQLStatementBuilder;
import org.smartbit4all.sql.SQLStatementBuilderIF;
import org.smartbit4all.sql.SQLTableNode;
import org.smartbit4all.sql.SQLWhere;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * The delete execution is an object containing all the data and objects for the execution.
 *
 * @author Peter Boros
 *
 * @param <E>
 */
public class SQLDeleteExecution<E extends EntityDefinition> {

  private static final Logger log = LoggerFactory.getLogger(SQLDeleteExecution.class);

  private DeleteInput<E> input;

  /**
   * The JDBC connection accessor.
   */
  private JdbcTemplate jdbcTemplate;

  /**
   * The delete statement.
   */
  private SQLDeleteStatement delete;

  /**
   * This is the number of records that are executed at the same time using the
   * {@link PreparedStatement#addBatch()}. Default batch size is defined in SQLBatchUtils. If set to
   * 1, batch execution is disabled.
   */
  private int batchExecutionSize = SQLBatchUtils.DEFAULT_BATCH_SIZE;

  private SQLDBParameter sqlDBParameter;

  public SQLDeleteExecution(JdbcTemplate jdbcTemplate, DeleteInput<E> input,
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
  public SQLDeleteExecution<E> setBatchExecutionSize(int batchSize) {
    this.batchExecutionSize = SQLBatchUtils.validateBatchSize(batchSize);
    return this;
  }

  public DeleteOutput execute() {
    String schema = CrudApis.getCrudApi().getSchema(input.getEntityDefinition());

    SQLStatementBuilderIF builder = new SQLStatementBuilder(sqlDBParameter);
    TableDefinition table = input.getEntityDefinition().tableDefinition();
    SQLTableNode tableNode = new SQLTableNode(schema, table.getName());
    delete = new SQLDeleteStatement(tableNode);

    // Set the where to have the criterion for the delete.
    Expression deleteCriterion = null;

    List<PropertyOwned<?>> identifiedBy = input.identifiedBy();
    final List<Expression> identifierExpressions = new ArrayList<>(identifiedBy.size());
    for (PropertyOwned<?> property : identifiedBy) {
      Expression exp = property.eq(null);
      identifierExpressions.add(exp);
      if (deleteCriterion == null) {
        deleteCriterion = exp;
      } else {
        deleteCriterion = deleteCriterion.AND(exp);
      }
    }

    // Add the where to the delete statement.
    SQLWhere where = new SQLWhere(deleteCriterion);
    delete.setWhere(where);
    delete.render(builder);

    input.start();

    // Handle single row operations (no batching)
    if (input.size() == 1 ||
        !SQLBatchUtils.useBatchProcessing(batchExecutionSize)) {
      int totalCount = 0;
      for (int row = 0; row < input.size(); row++) {
        setBindValues(identifierExpressions, row);
        SQLPreparedStatementCreator psc = new SQLPreparedStatementCreator(builder, delete);
        totalCount += jdbcTemplate.update(psc);
      }
      return new DeleteOutput(totalCount);
    }

    // Handle batch operations
    int totalCount = 0;
    int offset = 0;
    int remaining = input.size();
    while (remaining > 0) {
      int currentBatchSize = Math.min(batchExecutionSize, remaining);
      int count = executeBatch(builder, identifierExpressions, offset, currentBatchSize);
      if (count != currentBatchSize) {
        log.warn("count != currentBatchSize! {} != {}", count, currentBatchSize);
      }
      offset += currentBatchSize;
      remaining -= currentBatchSize;
      totalCount += count;
    }
    return new DeleteOutput(totalCount);
  }

  private void setBindValues(final List<Expression> identifierExpressions, int row) {
    for (int i = 0; i < identifierExpressions.size(); i++) {
      Expression2Operand<?> exp = (Expression2Operand<?>) identifierExpressions.get(i);
      exp.getLiteral().setValueUnchecked(input.getIdValue(row, i));
    }
  }

  private int executeBatch(SQLStatementBuilderIF builder,
      final List<Expression> identifierExpressions, int offset, int batchSize) {

    return SQLBatchUtils.executeBatch(jdbcTemplate, builder.getStatement(),
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws java.sql.SQLException {
            setBindValues(identifierExpressions, offset + i);
            delete.bind(builder, ps);
          }

          @Override
          public int getBatchSize() {
            return batchSize;
          }
        });
  }

}
