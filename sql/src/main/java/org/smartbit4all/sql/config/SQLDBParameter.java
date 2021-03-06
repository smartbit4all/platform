/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
