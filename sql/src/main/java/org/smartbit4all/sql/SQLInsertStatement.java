package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.smartbit4all.domain.meta.PropertyOwned;

/**
 * The insert statement with every data for rendering and binding it.
 * 
 * @author Peter Boros
 */
public final class SQLInsertStatement implements SQLStatementNode {

  /**
   * The table for the statement.
   */
  SQLTableNode table;

  /**
   * The columns to be set during the insert.
   */
  private SQLInsertColumns columns = new SQLInsertColumns();

  /**
   * The values in the same order then in the columns.
   */
  private SQLInsertValues values = new SQLInsertValues();

  /**
   * Constructs a new insert statement.
   * 
   * @param table The table for the insert statement.
   */
  public SQLInsertStatement(SQLTableNode table) {
    this.table = table;
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    values.bind(b, stmt);
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.insertInto(table);
    columns.render(b);
    values.render(b);
  }

  public final SQLBindValue addColumn(String columnName, PropertyOwned<?> propertyColumn) {
    columns.addColumn(columnName);
    SQLBindValue result = new SQLBindValue(propertyColumn);
    values.nodes.add(result);
    return result;
  }

}
