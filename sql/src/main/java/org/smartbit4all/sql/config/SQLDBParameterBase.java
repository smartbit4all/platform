/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.sql.config;

import org.smartbit4all.domain.utility.SupportedDatabase;

/**
 * The basic implementation of the database configurations. Storing the settings and adding generic
 * utility functions.
 * 
 * @author Peter Boros
 */
public class SQLDBParameterBase implements SQLDBParameter {

  public static final String SB4DSSTRING = "SB4DSSTRING";

  public static final String SB4DSINT = "SB4DSINT";

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

  @Override
  public String getIntegerDataSetTableName() {
    return SB4DSINT;
  }

  @Override
  public String getStringDataSetTableName() {
    return SB4DSSTRING;
  }

}
