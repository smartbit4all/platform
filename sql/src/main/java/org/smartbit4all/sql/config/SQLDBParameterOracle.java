package org.smartbit4all.sql.config;

import org.smartbit4all.domain.utility.SupportedDatabase;

/**
 * The ORACLE implementation of the database configuration.
 * 
 * @author Peter Boros
 */
public class SQLDBParameterOracle extends SQLDBParameterBase {

  public SQLDBParameterOracle() {
    super();
    type = SupportedDatabase.ORACLE;
    datetimeSQL = "select sysdate from dual";
  }

}
