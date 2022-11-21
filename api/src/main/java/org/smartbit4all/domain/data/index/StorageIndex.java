package org.smartbit4all.domain.data.index;

import org.smartbit4all.domain.meta.Expression;

public interface StorageIndex {

  /**
   * @param expression
   * @return True if the index is useful for identifying the matching records by the expression.
   */
  boolean canUseFor(Expression expression);

}
