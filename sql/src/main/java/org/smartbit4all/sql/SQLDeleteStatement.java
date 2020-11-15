package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The delete SQL statement.
 * 
 * @author Peter Boros
 */
public class SQLDeleteStatement implements SQLStatementNode {


  /**
   * The table for the statement.
   */
  SQLTableNode table;

  /**
   * This is the where for the delete statement.
   */
  private SQLWhere where = null;

  /**
   * Constructs a delete statement.
   * 
   * @param table The table of the statement.
   */
  public SQLDeleteStatement(SQLTableNode table) {
    this.table = table;
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    if (where != null) {
      where.bind(b, stmt);
    }
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.deleteFrom(table);
    if (where != null) {
      b.preProcessWhere();
      where.render(b);
    }
  }

  public SQLWhere getWhere() {
    return where;
  }

  public void setWhere(SQLWhere where) {
    this.where = where;
  }

}
