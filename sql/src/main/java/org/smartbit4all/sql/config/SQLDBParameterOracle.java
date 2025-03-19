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

import static org.smartbit4all.domain.meta.PropertyFunction.TRUNCATE_PREFIX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.domain.meta.PropertyFunction;
import org.smartbit4all.domain.meta.PropertyFunction.Builder;
import org.smartbit4all.domain.utility.SupportedDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * The ORACLE implementation of the database configuration.
 * 
 * @author Peter Boros
 */
public class SQLDBParameterOracle extends SQLDBParameterBase {

  private static final Logger log = LoggerFactory.getLogger(SQLDBParameterOracle.class);

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

  @Override
  protected void createTemTable(JdbcTemplate jdbcTemplate, String tableName, String columnType) {
    jdbcTemplate.execute("CREATE GLOBAL TEMPORARY TABLE " + tableName
        + " (ID NUMBER(18) NOT NULL, VAL " + columnType + " NULL)");
    jdbcTemplate.execute("CREATE INDEX " + tableName
        + "_ID_IDX ON " + tableName + " (ID)");
    jdbcTemplate.execute("CREATE INDEX " + tableName
        + "_VAL_IDX ON " + tableName + " (VAL)");

  }

  @Override
  public PropertyFunction convertPropertyFunction(PropertyFunction function) {
    String functionName = function.getName().toLowerCase();
    if (functionName.startsWith(TRUNCATE_PREFIX)) {
      String unitOfTime = functionName.substring(TRUNCATE_PREFIX.length());
      if (PropertyFunction.SECOND_POSTFIX.equals(unitOfTime)) {
        // TO_DATE(TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS')
        // @formatter:off
       return PropertyFunction.build("TO_DATE")
            .addInnerFunction("TO_CHAR")
              .selfPropertyParam()
              .stringParam("YYYY-MM-DD HH24:MI:SS")
              .closeInnerFunction()
            .stringParam("YYYY-MM-DD HH24:MI:SS")
            .build();
       // @formatter:off
      } else {
        Builder truncFunction = PropertyFunction.build("TRUNC")
            .selfPropertyParam();
        switch (unitOfTime) {
          case PropertyFunction.MINUTE_POSTFIX:
            // TRUNC(SYSDATE, 'MI')
            truncFunction.stringParam("MI");
            break;
          case PropertyFunction.HOUR_POSTFIX:
            // TRUNC(SYSDATE, 'HH')
            truncFunction.stringParam("HH");
            break;
          case PropertyFunction.MONTH_POSTFIX:
            // TRUNC(SYSDATE, 'MM')
            truncFunction.stringParam("MM");
            break;
          case PropertyFunction.YEAR_POSTFIX:
            // TRUNC(SYSDATE, 'YYYY')
            truncFunction.stringParam("YYYY");
            break;
          case PropertyFunction.DAY_POSTFIX:
          default:
            // TRUNC(SYSDATE)
            log.warn("Unrecognised truncate unitOfTime ({}), using day", unitOfTime);
            break;
        }
        return truncFunction.build();
      }
    }
    return super.convertPropertyFunction(function);
  }

}
