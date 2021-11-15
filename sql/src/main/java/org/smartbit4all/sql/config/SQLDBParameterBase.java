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
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * The basic implementation of the database configurations. Storing the settings and adding generic
 * utility functions.
 * 
 * @author Peter Boros
 */
public abstract class SQLDBParameterBase implements SQLDBParameter {

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
  public abstract String getDatetimeSQL();

  @Override
  public String getTableRownumSQL(String table) {
    return "select count(1) where exists (select * from " + table + ");";
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

  protected void createTemTable(JdbcTemplate jdbcTemplate, String tableName, String columnType) {
    jdbcTemplate.execute("CREATE TABLE " + tableName
        + " (ID NUMBER(18) NOT NULL, VAL " + columnType + " NULL)");
    jdbcTemplate.execute("CREATE INDEX " + tableName
        + "_ID_IDX ON " + tableName + " (ID)");
    jdbcTemplate.execute("CREATE INDEX " + tableName
        + "_VAL_IDX ON " + tableName + " (VAL)");

  }

  @Override
  public void createIntegerDataSetTable(JdbcTemplate jdbcTemplate) {
    createTemTable(jdbcTemplate, SQLDBParameterBase.SB4DSINT, "NUMBER(38)");
  }

  @Override
  public void createStringDataSetTable(JdbcTemplate jdbcTemplate) {
    createTemTable(jdbcTemplate, SQLDBParameterBase.SB4DSSTRING, "VARCHAR2(1000)");
  }

  @Override
  public int saveInDataSetLimit() {
    return 250;
  }

}
