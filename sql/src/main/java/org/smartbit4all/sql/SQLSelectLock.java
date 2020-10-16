package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The lock adds instruction to the select statement if we would like to lock the selected records
 * during the database transaction. The database specific statement modifier will be added to the
 * statement we we have this parameter.
 * 
 * <ul>
 * <li>ORACLE: FOR UPDATE WAIT / NOWAIT</li>
 * <li>PostgreSQL: FOR UPDATE WAIT / NOWAIT + SET LOCAL lock_timeout =</li>
 * <li>DB2: FOR UPDATE - NOT IMPLEMENTED YET!</li>
 * <li>SQLMX: FOR SERIALIZABLE ACCESS IN EXCLUSIVE MODE</li>
 * </ul>
 * 
 * @author Peter Boros
 */
public class SQLSelectLock implements SQLStatementNode {

  /**
   * The lock timeout for the lock request.
   * <ul>
   * <li>-1 : Wait forever without timeout.</li>
   * <li>0 : No wait - if any of the rows are locked then throw an exception immediately.</li>
   * <li>>0 : Wait until the timeout. The timeout is in millisecond.</li>
   * </ul>
   */
  private long timeoutInMillis;

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.appendLock(timeoutInMillis);
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    // NOP
  }

  public SQLSelectLock(long timeoutInMillis) {
    super();
    this.timeoutInMillis = timeoutInMillis;
  }

}
