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

/**
 * The H2 implementation of the database parameter.
 *
 * @author Peter Boros
 */
public class SQLDBParameterH2 extends SQLDBParameterBase {

  public SQLDBParameterH2() {
    super();
    type = SupportedDatabase.ORACLE;
  }

  @Override
  public String getDatetimeSQL() {
    return "select now()";
  }

  @Override
  public String getTableNamesSQL() {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT table_name ")
        .append("FROM INFORMATION_SCHEMA.TABLES ")
        .append("WHERE ");
    if (schema != null && !DEFAULT.equals(schema)) {
      sb.append("owner='").append(schema).append("' AND ");
    }
    sb.append("table_type='TABLE';");
    return sb.toString();
  }


  @Override
  public PropertyFunction convertPropertyFunction(PropertyFunction function) {
    String functionName = function.getName().toLowerCase();
    if (functionName.startsWith("truncate_")) {
      String unitOfTime = functionName.substring("truncate_".length());
      // DATE_TRUNC('SECOND', dateTimeColumn)
      return PropertyFunction
          .build("DATE_TRUNC")
          .stringParam(unitOfTime)
          .selfPropertyParam()
          .build();
    }
    return super.convertPropertyFunction(function);
  }

}
