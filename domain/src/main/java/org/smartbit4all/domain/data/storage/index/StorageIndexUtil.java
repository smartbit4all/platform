package org.smartbit4all.domain.data.storage.index;

import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.Property;

public class StorageIndexUtil {

  public static <F> boolean twoOperandPropertyIndex(Property<F> valueField, Expression expression) {
    if (expression instanceof Expression2Operand<?>) {
      Expression2Operand<?> expression2operand = (Expression2Operand<?>) expression;
      if (valueField == expression2operand.getOp().property()) {
        return true;
      }
    }
    return false;
  }
  
}
