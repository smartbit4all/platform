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
package org.smartbit4all.sql.service.modify;

import java.sql.PreparedStatement;
import java.sql.Statement;

public class SQLModifyUtility {

  /**
   * Executes the batch that was collected in the {@link Statement} and summarize the number of
   * effected records.
   * 
   * @param stmt A JDBC statement that collected the batch record by calling the
   *        {@link PreparedStatement#addBatch()}.
   * @return The number of effected rows. In case of given databases it could fail! In this case we
   *         assume that the operation was succeeded.
   * @throws Exception
   */
  static final int executeBatch(PreparedStatement stmt) throws Exception {
    int uc = 0;
    int updateCounts[] = stmt.executeBatch();
    for (int i = 0; i < updateCounts.length; i++) {
      if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
        uc++;
      } else if (updateCounts[i] > 0) {
        uc += updateCounts[i];
      }
    }
    stmt.clearBatch();
    return uc;
  }

}
