package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The group by node of the statement.
 * 
 * @author Peter Boros
 *
 */
public class SQLGroupBy extends SQLNodeList<SQLGroupByColumn> {

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.groupBy();
    super.render(b);
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // No need to bind.
  }

}
