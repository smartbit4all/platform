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
 * The ORACLE implementation of the database configuration.
 * 
 * @author Peter Boros
 */
public class SQLDBParameterOracle extends SQLDBParameterBase {

  public SQLDBParameterOracle() {
    super();
    type = SupportedDatabase.ORACLE;
  }

  @Override
  public String getDatetimeSQL() {
    return "select sysdate from dual";
  }

  @Override
  public String getTableNamesSQL() {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT table_name ")
        .append("FROM user_tables ");
    if (schema != null) {
      sb.append("WHERE owner='").append(schema);
    }
    return sb.toString();
  }

}
