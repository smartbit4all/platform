package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The table node encapsulate the table name and the schema handling. We can have tables in every
 * SQL statement but at different positions. It's usually used by the SQL Select statement.
 * 
 * @author Peter Boros
 */
public class SQLTableNode implements SQLStatementNode {

  /**
   * The databases usually defines schemas. They can have different level of support but anyway from
   * the perspective of an SQL select they are namespaces. So if we know this namespace then we have
   * to use it to construct the qualified name of a table.
   */
  String schema;

  /**
   * The name of the table that is used in the SQL statement. Be careful with the case of this
   * String. It must match with the database table name handling. If you need case sensitive table
   * names then use {@link StringConstant#DOUBLE_QUOTE} inside the name!
   */
  String name;

  /**
   * Constructs a table node.
   * 
   * @param schema The database schema.
   * @param name The name of the table.
   */
  public SQLTableNode(String schema, String name) {
    super();
    this.schema = schema;
    this.name = name;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(this);
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // There is no need to bind.
  }

}
