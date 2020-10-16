package org.smartbit4all.sql;

import java.lang.ref.WeakReference;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A group by column is an item in the select.
 * 
 * @author Peter Boros
 */
public class SQLGroupByColumn implements SQLStatementNode {

  /**
   * Every column belongs to a from section. It defines the alias of the column. It's a
   * {@link WeakReference} to help gc mechanism.
   */
  WeakReference<SQLSelectFromNode> from;

  /**
   * The "name" of the column.
   */
  String columnName;

  /**
   * Constructs a new group by column.
   * 
   * @param from
   * @param columnName The column name.
   */
  public SQLGroupByColumn(SQLSelectFromNode from, String columnName) {
    super();
    this.from = new WeakReference<>(from);
    this.columnName = columnName;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(this);
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // There is nothing to bind.
  }

}
