/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.smartbit4all.domain.service.modify.UpdateImpl;
import org.smartbit4all.domain.service.query.Queries;
import org.smartbit4all.domain.utility.SupportedDatabase;
import org.smartbit4all.sql.SQLBindValue;
import org.smartbit4all.sql.SQLStatementBuilder;
import org.smartbit4all.sql.SQLStatementBuilderIF;
import org.smartbit4all.sql.SQLTableNode;
import org.smartbit4all.sql.SQLUpdateStatement;
import org.smartbit4all.sql.SQLWhere;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

public class SQLUpdate<E extends EntityDefinition> extends UpdateImpl<E> {

  /**
   * The JDBC connection accessor.
   */
  JdbcTemplate jdbcTemplate;

  /**
   * The update statement.
   */
  SQLUpdateStatement update;

  /**
   * This is the number of records that are executed at the same time using the
   * {@link PreparedStatement#addBatch()}. By default it's 1 and it means we don't use
   * {@link PreparedStatement#executeBatch()} but rather {@link PreparedStatement#execute()}.
   */
  int batchExecutionSize = 1;

  public SQLUpdate(JdbcTemplate jdbcTemplate, E entityDef) {
    this.jdbcTemplate = jdbcTemplate;
    this.entityDef = entityDef;
  }

  @Override
  public void execute() throws Exception {
    String schema = Queries.getQueryApi().getSchema(this.entityDef);
    
    // SQLStatementBuilderIF builder = entityDef.context().get(SQLStatementBuilderIF.class);
    SQLStatementBuilderIF builder = new SQLStatementBuilder(SupportedDatabase.ORACLE);
    TableDefinition table = entityDef.tableDefinition();
    SQLTableNode tableNode = new SQLTableNode(schema, table.getName());
    update = new SQLUpdateStatement(tableNode);
    // First add the values and set the where to have the criterion for the update.
    Expression updateCriterion = null;
    List<SQLBindValue> values = new ArrayList<>();
    for (PropertyOwned<?> property : input.properties()) {
      values.add(update.addColumn(property));
    }

    List<PropertyOwned<?>> identifiedBy = input.identifiedBy();
    List<Expression> identifierExpressions = new ArrayList<>(2);
    for (PropertyOwned<?> property : identifiedBy) {
      Expression exp = property.eq(null);
      identifierExpressions.add(exp);
      if (updateCriterion == null) {
        updateCriterion = exp;
      } else {
        updateCriterion.AND(exp);
      }
    }

    // Add the where to the update statement.
    SQLWhere where = new SQLWhere(updateCriterion);
    update.setWhere(where);
    update.render(builder);


    input.start();


    while (input.next()) {
      PreparedStatementCreator psc = new SQLPreparedStatementCreator(builder, update);

      for (int i = 0; i < values.size(); i++) {
        SQLBindValue bindValue = values.get(i);
        bindValue.setValue(input.get(i));
      }
      for (int i = 0; i < identifierExpressions.size(); i++) {
        Expression2Operand<?> exp = (Expression2Operand<?>) identifierExpressions.get(i);
        exp.getLiteral().setValueUnchecked(input.getIdentifier(i));
      }

      jdbcTemplate.update(psc);

    }


    // TODO use jdbcTemplate methods instead of connection!
    // try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
    // PreparedStatement stmt = connection.prepareStatement(builder.getStatement());
    //
    // int batchCount = 0;
    // while (input.next()) {
    //
    // for (int i = 0; i < values.size(); i++) {
    // SQLBindValue bindValue = values.get(i);
    // bindValue.setValue(input.get(i));
    // }
    // for (int i = 0; i < identifierExpressions.size(); i++) {
    // Expression2Operand<?> exp = (Expression2Operand<?>) identifierExpressions.get(i);
    // exp.getLiteral().setValueUnchecked(input.getIdentifier(i));
    // }
    //
    // update.bind(builder, stmt);
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
