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

import org.smartbit4all.core.utility.ListBasedMap;
import org.smartbit4all.domain.meta.Expression;

/**
 * If we think about the select as a graph of from tables then the join is the edge of this graph.
 * When we render the SQL statement we traverse this graph in a depth first search mode.
 * 
 * @author Peter Boros
 */
public class SQLSelectJoin {

  /**
   * This referenceName, the name of the reference must be unique among the joins of a table. It's
   * mandatory to be able to used by the {@link QueryRequest}.
   */
  private String referenceName;

  /**
   * The target node of the join. We use the {@link SQLSelectFromTableNode#alias()} to qualify the
   * columns in the join.
   */
  SQLSelectFromNode target;

  /**
   * Defines if the SQL join is outer or not.
   */
  boolean outer;

  /**
   * The join between the properties of the source and the target. The key is the property of the
   * source and the value is from the target. This is typically one to one mapping so we use this
   * special map to avoid unnecessary map administration.
   */
  ListBasedMap<String, String> joinColumns = new ListBasedMap<>();

  /**
   * If we need to filter the joined target columns with additional conditions then this condition
   * must refer only the column of the target!
   * 
   * TODO Not used yet
   */
  Expression additionalTargetCondition;

  /**
   * Constructs a new join.
   * 
   * @param target
   * @param outer
   */
  public SQLSelectJoin(SQLSelectFromNode source, SQLSelectFromNode target, boolean outer) {
    super();
    this.target = target;
    this.outer = outer;
  }

  /**
   * Add a join condition where the two columns are equal.
   * 
   * @param sourceColumn
   * @param targetColumn
   */
  public void addJoin(String sourceColumn, String targetColumn) {
    joinColumns.put(sourceColumn, targetColumn);
  }

  /**
   * This identifier typically the name of the reference must be unique among the joins of a table.
   * It's mandatory to be able to used by the {@link QueryRequest}.
   * 
   * @return
   */
  public String getReferenceName() {
    return referenceName;
  }

  /**
   * This identifier typically the name of the reference must be unique among the joins of a table.
   * It's mandatory to be able to used by the {@link QueryRequest}.
   * 
   * @param referenceName
   */
  public void setReferenceName(String referenceName) {
    this.referenceName = referenceName;
  }

  /**
   * The target table node of the join.
   * 
   * @return
   */
  public SQLSelectFromNode target() {
    return target;
  }

}
