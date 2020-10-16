package org.smartbit4all.sql.config;

import org.smartbit4all.domain.utility.SupportedDatabase;

/**
 * The ORACLE implementation of the database parameter.
 * 
 * @author Peter Boros
 */
public class SQLDBParameterH2 extends SQLDBParameterBase {

  public SQLDBParameterH2() {
    super();
    type = SupportedDatabase.ORACLE;
    datetimeSQL = "select now()";
  }

}
