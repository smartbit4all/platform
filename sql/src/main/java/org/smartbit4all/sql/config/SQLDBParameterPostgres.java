package org.smartbit4all.sql.config;

import org.smartbit4all.domain.utility.SupportedDatabase;

/**
 * The ORACLE implementation of the database configuration.
 * 
 * @author Peter Boros
 */
public class SQLDBParameterPostgres extends SQLDBParameterBase {

  public SQLDBParameterPostgres() {
    super();
    type = SupportedDatabase.POSTGRESQL;
    datetimeSQL = "select now()";
  }

}
