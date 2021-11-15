package org.smartbit4all.sql.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseStateServiceImpl implements DatabaseStateService {

  @Autowired
  private SQLDBParameter sqlDbParameter;

  @Override
  public boolean isTableEmpty(DataSource dataSource, String tableName) {

    boolean isTableEmpty = true;
    try (Connection conn = dataSource.getConnection();
        Statement statement = conn.createStatement();) {

      String selectSequence = sqlDbParameter.getTableRownumSQL(tableName);
      ResultSet resultSet = statement.executeQuery(selectSequence);

      resultSet.next();
      long count = resultSet.getLong(1);
      if (count > 0) {
        isTableEmpty = false;
      }

    } catch (Exception e) {
      throw new RuntimeException("Unable to query table count for table: " + tableName, e);
    }

    return isTableEmpty;
  }

  @Override
  public boolean areDataTablesEmpty(DataSource dataSource) {
    boolean isDbEmpty = true;
    try (Connection conn = dataSource.getConnection();
        Statement statement = conn.createStatement();) {

      String selectSequence = sqlDbParameter.getTableNamesSQL();
      ResultSet resultSet = statement.executeQuery(selectSequence);


      while (resultSet.next()) {
        String tableName = resultSet.getString(1);
        if (!isTableEmpty(dataSource, tableName)) {
          return false;
        }
      }

    } catch (Exception e) {
      throw new RuntimeException("Unable to query table names", e);
    }

    return isDbEmpty;
  }


}
