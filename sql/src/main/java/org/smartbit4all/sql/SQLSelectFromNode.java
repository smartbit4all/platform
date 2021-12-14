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
   * @param referenceName The name of the {@link Reference} we are looking for.
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
   * @param join Can be null if we are the root.
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

