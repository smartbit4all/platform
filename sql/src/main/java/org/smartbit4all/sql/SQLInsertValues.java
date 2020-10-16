package org.smartbit4all.sql;

import org.smartbit4all.core.utility.StringConstant;

/**
 * The values section with real bound values.
 * 
 * @author Peter Boros
 */
public class SQLInsertValues extends SQLNodeList<SQLCommonValueNode>
    implements SQLInsertValuesSection {

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.startValues();
    super.render(b);
    b.append(StringConstant.RIGHT_PARENTHESIS);
  }

}
