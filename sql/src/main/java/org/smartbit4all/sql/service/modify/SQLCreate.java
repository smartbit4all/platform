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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinition.TableDefinition;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.service.modify.CreateImpl;
import org.smartbit4all.domain.utility.SupportedDatabase;
import org.smartbit4all.sql.SQLBindValue;
import org.smartbit4all.sql.SQLInsertStatement;
import org.smartbit4all.sql.SQLStatementBuilder;
import org.smartbit4all.sql.SQLStatementBuilderIF;
import org.smartbit4all.sql.SQLTableNode;
import org.springframework.jdbc.core.InterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

public class SQLCreate<E extends EntityDefinition> extends CreateImpl<E> {

  /**
   * The JDBC connection accessor.
   */
  JdbcTemplate jdbcTemplate;

  /**
   * The insert statement.
   */
  SQLInsertStatement insert;

  /**
   * This is the number of records that are executed at the same time using the
   * {@link PreparedStatement#addBatch()}. By default it's 1 and it means we don't use
   * {@link PreparedStatement#executeBatch()} but rather {@link PreparedStatement#execute()}.
   */
  int batchExecutionSize = 1;

  public SQLCreate(JdbcTemplate jdbcTemplate, E entityDef) {
    this.jdbcTemplate = jdbcTemplate;
    this.entityDef = entityDef;
  }

  private final class PreparedStatementSetter
      implements InterruptibleBatchPreparedStatementSetter {

    List<SQLBindValue> values;

    List<SQLBindValue> identifiers;

    SQLStatementBuilderIF builder;

    boolean hasNext;

    public PreparedStatementSetter(SQLStatementBuilderIF builder, List<SQLBindValue> values,
        List<SQLBindValue> identifiers) {
      super();
      this.builder = builder;
      this.values = values;
      this.identifiers = identifiers;
      // Start reading the source.
      input.start();
      hasNext = input.next();
    }

    @Override
    public void setValues(PreparedStatement ps, int idx) throws SQLException {
      for (int i = 0; i < values.size(); i++) {
        SQLBindValue bindValue = values.get(i);
        bindValue.setValue(input.get(i));
      }
      for (int i = 0; i < identifiers.size(); i++) {
        SQLBindValue bindValue = identifiers.get(i);
        bindValue.setValue(input.getIdentifier(i));
      }
      insert.bind(builder, ps);
      hasNext = input.next();
    }

    @Override
    public int getBatchSize() {
      return batchExecutionSize;
    }

    @Override
    public boolean isBatchExhausted(int i) {
      return !hasNext;
    }

  }

  @Override
  public void execute() throws Exception {
    // SQLStatementBuilderIF builder = entityDef.context().get(SQLStatementBuilderIF.class);
    SQLStatementBuilderIF builder = new SQLStatementBuilder(SupportedDatabase.ORACLE);
    TableDefinition table = entityDef.tableDefinition();
    SQLTableNode tableNode = new SQLTableNode(table.getSchema(), table.getName());
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

    // -------------------------

    // Execute the before statements if there are any.
    for (Entry<String, StringBuilder> entry : builder.beforeStatements().entrySet()) {
      String beforeStatement = entry.getValue().toString();
      if (beforeStatement != null) {
        jdbcTemplate.execute(beforeStatement);
      }
    }

    // PreparedStatementSetter statementSetter =
    // new PreparedStatementSetter(builder, values, identifiers);
    //
    // while (statementSetter.hasNext) {
    // jdbcTemplate.batchUpdate(builder.getStatement(),
    // statementSetter);
    // }



    input.start();


    while (input.next()) {
      PreparedStatementCreator psc = new SQLPreparedStatementCreator(builder, insert);

      for (int i = 0; i < values.size(); i++) {
        SQLBindValue bindValue = values.get(i);
        bindValue.setValue(input.get(i));
      }
      for (int i = 0; i < identifiers.size(); i++) {
        SQLBindValue bindValue = identifiers.get(i);
        bindValue.setValue(input.getIdentifier(i));
      }

      jdbcTemplate.update(psc);

    }


    // try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
    // PreparedStatement stmt = connection.prepareStatement(builder.getStatement());
    //
    // int batchCount = 0;
    // source.start();
    // while (source.next()) {
    //
    // for (int i = 0; i < values.size(); i++) {
    // SQLBindValue bindValue = values.get(i);
    // bindValue.setValue(source.get(i));
    // }
    // for (int i = 0; i < identifiers.size(); i++) {
    // SQLBindValue bindValue = identifiers.get(i);
    // bindValue.setValue(source.getIdentifier(i));
    // }
    // insert.bind(builder, stmt);
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
