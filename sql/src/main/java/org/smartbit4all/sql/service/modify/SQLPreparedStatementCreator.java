package org.smartbit4all.sql.service.modify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.smartbit4all.sql.SQLStatementBuilderIF;
import org.smartbit4all.sql.SQLStatementNode;
import org.springframework.jdbc.core.PreparedStatementCreator;

final class SQLPreparedStatementCreator implements PreparedStatementCreator {
  private final SQLStatementBuilderIF builder;
  PreparedStatement ps = null;
  SQLStatementNode sqlStatement;

  SQLPreparedStatementCreator(SQLStatementBuilderIF builder,
      SQLStatementNode sqlStatement) {
    this.builder = builder;
    this.sqlStatement = sqlStatement;
  }

  @Override
  public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
    if (ps == null) {
      ps = con.prepareStatement(builder.getStatement());
    }
    sqlStatement.bind(builder, ps);
    return ps;
  }
}