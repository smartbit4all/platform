package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The order by section of the SQL statement.
 * 
 * @author Peter Boros
 */
public class SQLOrderBy extends SQLNodeList<SQLOrderByColumn> {

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.orderBy();
    super.render(b);
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // No need to bind.
  }

}
