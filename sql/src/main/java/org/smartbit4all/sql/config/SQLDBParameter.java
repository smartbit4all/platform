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

import org.smartbit4all.domain.meta.PropertyFunction;
import org.smartbit4all.domain.utility.SupportedDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

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

  String getTableRownumSQL(String table);

  String getTableNamesSQL();

  /**
   * If null then we skip the schema at SQL level.
   *
   * @return
   */
  String getSchema();

  /**
   * The integer data set table name in the database. In this table the value of the data set is an
   * integer number.
   *
   * @return
   */
  String getIntegerDataSetTableName();

  /**
   * The string data set table name in the database. In this table the value of the data set is a
   * string.
   *
   * @return
   */
  String getStringDataSetTableName();

  int saveInDataSetLimit();

  void createIntegerDataSetTable(JdbcTemplate jdbcTemplate);

  void createStringDataSetTable(JdbcTemplate jdbcTemplate);

  /**
   * This method convert a database independent PropertyFunction to a database specific, if
   * necessary. ANSI SQL function may not be converted, but database specific should be.
   *
   * @param function
   * @return
   */
  default PropertyFunction convertPropertyFunction(PropertyFunction function) {
    return function;
  }

}
