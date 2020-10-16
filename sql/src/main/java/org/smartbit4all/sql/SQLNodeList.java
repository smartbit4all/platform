package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.core.utility.StringConstant;


/**
 * 
 * This node list contains other nodes to render. This can be used as abstract super class for the
 * list based nodes like values list, columns etc.
 * 
 * @author Peter Boros
 */
public abstract class SQLNodeList<N extends SQLStatementNode> implements SQLStatementNode {

  /**
   * The node list of the given fragment.
   */
  List<N> nodes = new ArrayList<>();

  /**
   * Constructs a new node list.
   */
  public SQLNodeList() {
    super();
  }

  /**
   * The default implementation is simply produce a comma separated list from the nodes.
   */
  @Override
  public void render(SQLStatementBuilderIF b) {
    boolean firstNode = true;
    for (SQLStatementNode node : nodes) {
      if (!firstNode) {
        b.append(StringConstant.COMMA_SPACE);
      }
      node.render(b);
      firstNode = false;
    }
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    for (SQLStatementNode node : nodes) {
      node.bind(b, stmt);
    }
  }

}
