package org.smartbit4all.sql;

/**
 * The select from section with a table and an alias. These nodes are depending on each other with
 * joins and they form a directed acyclic graph. So we will have a root node as a starting point of
 * the traversal. The SQL itself is the result of a depth first search algorithm where the traversal
 * of a join edge means a join to the already joined structure.
 * 
 * @author Peter Boros
 */
public class SQLSelectFromTableNode extends SQLSelectFromNode {

  /**
   * The table in the from section of the select.
   */
  SQLTableNode table;

  /**
   * Constructs a new table node with the table and the alias we have.
   * 
   * @param table
   * @param alias
   */
  public SQLSelectFromTableNode(SQLTableNode table, String alias) {
    super(alias);
    this.table = table;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.append(table);
    b.append(SQLConstant.SEGMENTSEPARATOR);
    b.append(alias);
  }

}
