package org.smartbit4all.sql.config;

import org.smartbit4all.domain.utility.SupportedDatabase;

/**
 * The basic implementation of the database configurations. Storing the settings and adding generic
 * utility functions.
 * 
 * @author Peter Boros
 */
public class SQLDBParameterBase implements SQLDBParameter {

  /**
   * The default is the name of the default database configuration that is used by the modules by
   * default.
   */
  public static final String DEFAULT = "DEFAULTDBCONFIG";

  /**
   * The supported database type.
   */
  protected SupportedDatabase type;

  protected String name;

  protected String datetimeSQL;

  protected String schema;

  public SQLDBParameterBase() {
    super();
    // All the modules uses the default database configuration except setting something else for
    // them. So the basic configuration are registered as default to apply for all modules we have.
    name = DEFAULT;
  }

  @Override
  public SupportedDatabase getType() {
    return type;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDatetimeSQL() {
    return datetimeSQL;
  }

  @Override
  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

}
