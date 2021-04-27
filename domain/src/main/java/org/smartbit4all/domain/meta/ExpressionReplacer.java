package org.smartbit4all.domain.meta;

import java.util.List;
import org.smartbit4all.domain.meta.ExpressionFinder.FoundExpression;

/**
 * This utility is responsible for replacing a part of the expression.
 * 
 * @author Peter Boros
 */
public final class ExpressionReplacer {

  private ExpressionReplacer() {
    super();
  }

  public static final Expression replace(Expression fullExpression, Expression from,
      Expression to) {
    List<FoundExpression> foundList =
        ExpressionFinder.find(fullExpression, (Expression e) -> e == from);
    for (FoundExpression foundExpression : foundList) {
      if (foundExpression.getParent() == null) {
        return to;
      }
      foundExpression.getParent().replace(from, to);
    }
    return fullExpression;
  }

}
