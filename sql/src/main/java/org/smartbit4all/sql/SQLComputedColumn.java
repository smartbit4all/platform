package org.smartbit4all.sql;

import org.smartbit4all.core.utility.StringConstant;

public class SQLComputedColumn extends SQLSelectColumn {

  public SQLComputedColumn(SQLSelectFromNode from, String columnName, String alias) {
    super(from, columnName, alias);
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(StringConstant.LEFT_PARENTHESIS);
    b.append(columnName);
    b.append(StringConstant.RIGHT_PARENTHESIS);
    b.append(StringConstant.SPACE);
    b.append(alias);
  }
  
}
