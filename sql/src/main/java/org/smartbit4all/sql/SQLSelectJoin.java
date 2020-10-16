package org.smartbit4all.sql;

import org.smartbit4all.core.utility.ListBasedMap;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.service.query.Query;

/**
 * If we think about the select as a graph of from tables then the join is the edge of this graph.
 * When we render the SQL statement we traverse this graph in a depth first search mode.
 * 
 * @author Peter Boros
 */
public class SQLSelectJoin {

  /**
   * This referenceName, the name of the reference must be unique among the joins of a table. It's
   * mandatory to be able to used by the {@link Query}.
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
   * It's mandatory to be able to used by the {@link Query}.
   * 
   * @return
   */
  public String getReferenceName() {
    return referenceName;
  }

  /**
   * This identifier typically the name of the reference must be unique among the joins of a table.
   * It's mandatory to be able to used by the {@link Query}.
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
