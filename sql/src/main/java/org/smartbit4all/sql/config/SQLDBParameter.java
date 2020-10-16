package org.smartbit4all.sql.config;

import org.smartbit4all.domain.utility.SupportedDatabase;

/**
 * The interface for SQL level Database parameter. It has predecessors for every supported database
 * at platform level. These configurations can be extended and parameterized for the projects.
 * 
 * @author Peter Boros
 */
public interface SQLDBParameter {

  /**
   * The supported type of the database.
   * 
   * @return
   */
  SupportedDatabase getType();

  /**
   * @return
   */
  String getName();

  /**
   * The date time select from the database in case of different databases.
   * 
   * @return
   */
  String getDatetimeSQL();

  /**
   * If null then we skip the schema at SQL level.
   * 
   * @return
   */
  String getSchema();

}
