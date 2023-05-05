package org.smartbit4all.sql.config;

import org.smartbit4all.domain.meta.PropertyFunction;
import org.smartbit4all.domain.utility.SupportedDatabase;

public class SQLDBParameterMssql extends SQLDBParameterBase {

  public SQLDBParameterMssql() {
    super();
    type = SupportedDatabase.MSSQL;
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
  public String getDatetimeSQL() {
    return "select getdate()";
  }

  @Override
  public PropertyFunction convertPropertyFunction(PropertyFunction function) {
    String functionName = function.getName().toLowerCase();
    if (functionName.startsWith("truncate_")) {
      String unitOfTime = functionName.substring("truncate_".length());
      // @formatter:off
      // DATEADD(SECOND, DATEDIFF(SECOND, '20000101', dateTimeColumn))
      return PropertyFunction
          .build("DATEADD")
          .param(unitOfTime)
          .addInnerFunction("DATEDIFF")
            .param(unitOfTime)
            .stringParam("20000101")
            .selfPropertyParam()
            .closeInnerFunction()
          .stringParam("20000101")
          .build();
      // @formatter:on
    }
    return super.convertPropertyFunction(function);
  }

}
