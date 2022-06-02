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
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinition.TableDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.service.CrudApis;
import org.smartbit4all.domain.service.modify.DeleteInput;
import org.smartbit4all.domain.service.modify.DeleteOutput;
import org.smartbit4all.domain.utility.SupportedDatabase;
import org.smartbit4all.sql.SQLDeleteStatement;
import org.smartbit4all.sql.SQLStatementBuilder;
import org.smartbit4all.sql.SQLStatementBuilderIF;
import org.smartbit4all.sql.SQLTableNode;
import org.smartbit4all.sql.SQLWhere;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

/**
 * The create execution is an object containing all the data and objects for the execution.
 * 
 * @author Peter Boros
 *
 * @param <E>
 */
public class SQLDeleteExecution<E extends EntityDefinition> {

  DeleteInput<E> input;

  /**
   * The JDBC connection accessor.
   */
  JdbcTemplate jdbcTemplate;

  /**
   * The delete statement.
   */
  SQLDeleteStatement delete;

  /**
   * This is the number of records that are executed at the same time using the
   * {@link PreparedStatement#addBatch()}. By default it's 1 and it means we don't use
   * {@link PreparedStatement#executeBatch()} but rather {@link PreparedStatement#execute()}.
   */
  int batchExecutionSize = 1;

  public SQLDeleteExecution(JdbcTemplate jdbcTemplate, DeleteInput<E> input) {
    this.jdbcTemplate = jdbcTemplate;
    this.input = input;
  }

  public DeleteOutput execute() {
    String schema = CrudApis.getCrudApi().getSchema(input.getEntityDefinition());

    SQLStatementBuilderIF builder = new SQLStatementBuilder(SupportedDatabase.ORACLE);
    TableDefinition table = input.getEntityDefinition().tableDefinition();
    SQLTableNode tableNode = new SQLTableNode(schema, table.getName());
    delete = new SQLDeleteStatement(tableNode);
    // Set the where to have the criterion for the update.
    Expression deleteCriterion = null;

    List<PropertyOwned<?>> identifiedBy = input.identifiedBy();
    List<Expression> identifierExpressions = new ArrayList<>(2);
    for (PropertyOwned<?> property : identifiedBy) {
      Expression exp = property.eq(null);
      identifierExpressions.add(exp);
      if (deleteCriterion == null) {
        deleteCriterion = exp;
      } else {
        deleteCriterion.AND(exp);
      }
    }

    // Add the where to the update statement.
    SQLWhere where = new SQLWhere(deleteCriterion);
    delete.setWhere(where);
    delete.render(builder);

    input.start();


    int count = 0;
    while (input.next()) {
      PreparedStatementCreator psc = new SQLPreparedStatementCreator(builder, delete);

      for (int i = 0; i < identifierExpressions.size(); i++) {
        Expression2Operand<?> exp = (Expression2Operand<?>) identifierExpressions.get(i);
        exp.getLiteral().setValueUnchecked(input.getIdentifier(i));
      }

      jdbcTemplate.update(psc);
      count++;

    }

    return new DeleteOutput(count);


    // TODO use jdbcTemplate methods instead of connection!
    // try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
    // PreparedStatement stmt = connection.prepareStatement(builder.getStatement());
    //
    // int batchCount = 0;
    // while (input.next()) {
    //
    // for (int i = 0; i < identifierExpressions.size(); i++) {
    // Expression2Operand<?> exp = (Expression2Operand<?>) identifierExpressions.get(i);
    // exp.getLiteral().setValueUnchecked(input.getIdentifier(i));
    // }
    //
    // delete.bind(builder, stmt);
    //
    // if (batchExecutionSize > 1) {
    // stmt.addBatch();
    // batchCount++;
    //
    // // If we reached the number of rows defined by the batchExecutionSize then we execute the
    // // whole batch at once.
    // if (batchCount == batchExecutionSize) {
    // executedCount += SQLModifyUtility.executeBatch(stmt);
    // batchCount = 0;
    // }
    // } else {
    // executedCount += stmt.executeUpdate();
    // }
    // }
    // // At the end of the iteration we check if any batch row left. If yes, then we execute the
    // // last
    // // batch.
    // if (batchCount > 0) {
    // executedCount += SQLModifyUtility.executeBatch(stmt);
    // }
    // }

  }

}
