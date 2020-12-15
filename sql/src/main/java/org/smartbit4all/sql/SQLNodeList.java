/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
