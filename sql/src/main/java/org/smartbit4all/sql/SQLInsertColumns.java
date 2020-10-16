package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.smartbit4all.core.utility.StringConstant;


/**
 *
 * The column list for the insert statement.
 * 
 * @author Peter Boros
 */
public class SQLInsertColumns extends SQLNodeList<SQLStringConstant> {

  /**
   * Add the column name as node to the nodes list.
   * 
   * @param columnName The name of the column.
   */
  public void addColumn(String columnName) {
    nodes.add(new SQLStringConstant(columnName));
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(StringConstant.SPACE);
    if (!nodes.isEmpty()) {
      b.append(StringConstant.LEFT_PARENTHESIS);
      super.render(b);
      b.append(StringConstant.RIGHT_PARENTHESIS);
    }
  }

  /**
   *
   */
  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // Can be empty because there cann't be anything to bind.
  }

}
