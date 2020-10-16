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
