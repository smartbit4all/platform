package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Group by statement for SqlComputed columns.
 * The main difference between the {@link SQLGroupByColumn} and this is it refers to
 * the alias of the computed column because it's present is guaranteed.
 * 
 * @author bhorvath
 */
public class SQLGroupByComputedColumn extends SQLGroupByColumn {

  /**
   * Constructs a new group by column.
   * 
   * @param from
   * @param columnName The column name.
   */
  public SQLGroupByComputedColumn(String computedColAlias) {
    super(null, computedColAlias);
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(columnName);
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // There is nothing to bind.
  }

}
