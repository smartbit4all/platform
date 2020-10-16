package org.smartbit4all.sql.service.modify;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinition.TableDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.service.modify.DeleteImpl;
import org.smartbit4all.domain.utility.SupportedDatabase;
import org.smartbit4all.sql.SQLDeleteStatement;
import org.smartbit4all.sql.SQLStatementBuilder;
import org.smartbit4all.sql.SQLStatementBuilderIF;
import org.smartbit4all.sql.SQLTableNode;
import org.smartbit4all.sql.SQLWhere;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

public class SQLDelete<E extends EntityDefinition> extends DeleteImpl<E> {

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

  public SQLDelete(JdbcTemplate jdbcTemplate, E entityDefinition) {
    this.jdbcTemplate = jdbcTemplate;
    this.entityDef = entityDefinition;
  }

  @Override
  public void execute() throws Exception {
    // SQLStatementBuilderIF builder = entityDef.context().get(SQLStatementBuilderIF.class);
    SQLStatementBuilderIF builder = new SQLStatementBuilder(SupportedDatabase.ORACLE);
    TableDefinition table = entityDef.tableDefinition();
    SQLTableNode tableNode = new SQLTableNode(table.getSchema(), table.getName());
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


    while (input.next()) {
      PreparedStatementCreator psc = new SQLPreparedStatementCreator(builder, delete);

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
