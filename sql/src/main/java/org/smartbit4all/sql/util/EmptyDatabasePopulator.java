package org.smartbit4all.sql.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;

/**
 * Using this Database Populator the added scripts will be run only when the database is first time
 * initialized aka has no tables created yet.
 * 
 * @author Balazs Horvath
 */
public class EmptyDatabasePopulator extends ResourceDatabasePopulator {

  @Override
  public void populate(Connection connection) throws ScriptException {
    boolean alreadyInitialized = false;
    try {
      ResultSet rset =
          connection.getMetaData().getTables(null, null, "%", new String[] {"TABLE"});
      if (rset.next()) {
        alreadyInitialized = true;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Can not check if database already initialized.", e);
    }
    if (!alreadyInitialized) {
      super.populate(connection);
    }
  }

}
