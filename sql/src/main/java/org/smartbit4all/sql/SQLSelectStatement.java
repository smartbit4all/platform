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

/**
 * The select SQL statement. Contains a join graph of the tables involved. Every edge is a join that
 * joins the two table. The traversal of the graph is the rendering of the statement.
 * 
 * @author Peter Boros
 */
public class SQLSelectStatement implements SQLStatementNode {

  /**
   * The privileged value for the {@link #queryLimit} to show that there is no limit.
   */
  private static final int UNLIMITED = -1;

  /**
   * The from node that is the first starting point of the select graph. All the nodes are available
   * by traversing the {@link SQLSelectJoin} edges of this graph.
   */
  private SQLSelectFromNode rootFromNode;

  /**
   * The select columns.
   */
  private final SQLSelectColumns columns = new SQLSelectColumns();

  /**
   * The order by section of the select statement.
   */
  private SQLOrderBy orderBy = null;

  /**
   * The group by section of the select statement.
   */
  private SQLGroupBy groupBy = null;

  /**
   * The where section of the select contains only the conditions and not the joins.
   */
  private SQLWhere where = null;

  /**
   * The lock contains instructions about the database level lock for the selected columns.
   */
  private SQLSelectLock lock = null;

  /**
   * The given database can offer some kind of hint to execute the select with a better performance.
   */
  private String executionHint;

  /**
   * The query limit.
   */
  private int queryLimit = UNLIMITED;

  /**
   * If the select must be distinct or not. The default is false.
   */
  private boolean distinctQuery = false;

  /**
   * Add a new column to the select.
   * 
   * @param column
   */
  public void addColumn(SQLSelectColumn column) {
    columns.nodes.add(column);
  }

  /**
   * Add an order by column to the select.
   * 
   * @param column
   */
  public void addOrderBy(SQLOrderByColumn column) {
    if (orderBy == null) {
      orderBy = new SQLOrderBy();
    }
    orderBy.nodes.add(column);
  }

  /**
   * Add a group by column to the select.
   * 
   * @param column
   */
  public void addGroupBy(SQLGroupByColumn column) {
    if (groupBy == null) {
      groupBy = new SQLGroupBy();
    }
    groupBy.nodes.add(column);
  }

  /**
   * Set the from section of the select. It's a graph and must contains all the from sections before
   * run {@link #render(SQLStatementBuilder)}.
   * 
   * @param root The root node that is the starting point of rendering.
   */
  public void setFrom(SQLSelectFromNode root) {
    this.rootFromNode = root;
  }

  /**
   * Sets the where section of the select statement.
   * 
   * @param where
   */
  public void setWhere(SQLWhere where) {
    this.where = where;
  }

  /**
   * Sets the lock instruction section for the select statement.
   * 
   * @param lock
   */
  public void setLock(SQLSelectLock lock) {
    this.lock = lock;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    b.setQueryLimit(queryLimit);
    b.setHasOrderBy(orderBy != null && !orderBy.nodes.isEmpty());
    b.select(distinctQuery);
    columns.render(b);
    b.from();
    rootFromNode.render(b, null, null);
    if (where != null) {
      b.preProcessWhere();
      b.preProcessSelectWhere();
      where.render(b);
      b.postProcessWhere();
    } else {
      b.insteadOfWhere();
    }
    if (groupBy != null) {
      groupBy.render(b);
    }
    if (orderBy != null) {
      orderBy.render(b);
    }
    if (lock != null) {
      lock.render(b);
    } else {
      // TODO Add the closing section if we don't have any lock.
      // DB2 requires specific postfix in this case.
    }
    b.postProcessSelect();
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    rootFromNode.bind(b, stmt);
    if (where != null) {
      where.bind(b, stmt);
    }
  }

  public void setExecutionHint(String executionHint) {
    this.executionHint = executionHint;
  }

  public String getExecutionHint() {
    return executionHint;
  }

  public int getQueryLimit() {
    return queryLimit;
  }

  public void setQueryLimit(int queryLimit) {
    this.queryLimit = queryLimit;
  }

  public final boolean isDistinctQuery() {
    return distinctQuery;
  }

  public final void setDistinctQuery(boolean distinctQuery) {
    this.distinctQuery = distinctQuery;
  }

}
