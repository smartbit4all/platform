package org.smartbit4all.sql;

/**
 * The column set list of the update statement.
 * 
 * @author Peter Boros
 */
public class SQLUpdateValues extends SQLNodeList<SQLBindValue> {

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.updateSet();
    int i = 0;
    int size = nodes.size();
    for (SQLBindValue valueNode : nodes) {
      i++;
      b.appendUpdateValue(valueNode, i < size);
    }
  }

}
