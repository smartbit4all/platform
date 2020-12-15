/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
