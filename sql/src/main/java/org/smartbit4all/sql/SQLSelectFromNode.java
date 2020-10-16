package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.meta.Reference;

/**
 * The common nodes for the select from section. Can be a database table or another select also.
 * 
 * @author Peter Boros
 */
public abstract class SQLSelectFromNode implements SQLStatementNode {

  /**
   * The alias of the given from section.
   */
  String alias;

  /**
   * The joins that refers to the joined nodes as target.
   */
  List<SQLSelectJoin> joins = new ArrayList<>();

  /**
   * Constructs a new node with the alias.
   * 
   * @param alias
   */
  SQLSelectFromNode(String alias) {
    super();
    this.alias = alias;
  }

  /**
   * The alias of the from for the qualification of the columns.
   * 
   * @return
   */
  public String alias() {
    return alias;
  }

  /**
   * The joins of the given node.
   * 
   * @return
   */
  public List<SQLSelectJoin> joins() {
    return joins;
  }

  /**
   * Effectively find the join starting from the current node.
   * 
   * @param reference The {@link Reference} we are looking for.
   * @return
   */
  public SQLSelectJoin findJoin(String referenceName) {
    if (referenceName == null) {
      return null;
    }
    for (SQLSelectJoin selectJoin : joins) {
      if (referenceName.equals(selectJoin.getReferenceName())) {
        return selectJoin;
      }
    }
    return null;
  }

  /**
   * A specific render function to traverse the whole join graph. Recursive!
   * 
   * @param b The builder.
   * @param source The source from node. Can be null if we are the root.
   * @param joins Can be null if we are the root.
   */
  public void render(SQLStatementBuilderIF b, SQLSelectFromNode source, SQLSelectJoin join) {
    if (source != null) {
      // If we have source then we add the join.
      b.append(source, this, join);
    } else {
      // If there is no source then we can simply render ourself.
      render(b);
    }
    // Then we call the render of our joins. It will be a depth first search by default.
    for (SQLSelectJoin next : joins) {
      next.target.render(b, this, next);
    }
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    for (SQLSelectJoin next : joins) {
      next.target.bind(b, stmt);
    }
  }

}

