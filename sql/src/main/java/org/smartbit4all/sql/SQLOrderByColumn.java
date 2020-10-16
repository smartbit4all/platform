package org.smartbit4all.sql;

import java.lang.ref.WeakReference;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * An order by column is an item in the select.
 * 
 * @author Peter Boros
 */
public class SQLOrderByColumn implements SQLStatementNode {

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
   * Defines if it's ascending or descending.
   */
  boolean asc;

  /**
   * Defines if we want the nulls first or at the last.
   */
  boolean nullsFirst = false;

  /**
   * Constructs a new group by column.
   * 
   * @param from
   * @param columnName The column name.
   */
  public SQLOrderByColumn(SQLSelectFromNode from, String columnName, boolean asc,
      boolean nullsFirst) {
    super();
    this.from = new WeakReference<>(from);
    this.columnName = columnName;
    this.asc = asc;
    this.nullsFirst = nullsFirst;
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
