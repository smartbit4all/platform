package org.smartbit4all.sql;

import org.smartbit4all.core.utility.StringConstant;

public class SQLSelectAggregateColumn extends SQLSelectColumn {

  public SQLSelectAggregateColumn(SQLSelectFromNode from, String columnName, String alias) {
    super(from, columnName, alias);
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(columnName);
    b.append(StringConstant.SPACE);
    b.append(alias);
  }

}
