package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * An active part of the statement that is not bound and means only a fix string. It will be added
 * to the statement as is.
 * 
 * @author Peter Boros
 */
public class SQLStringConstant implements SQLCommonValueNode {

  private final String stringConstant;

  public SQLStringConstant(String sqlTag) {
    super();
    this.stringConstant = sqlTag;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(stringConstant);
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // Can be emty there is no need to bind.
  }

}
