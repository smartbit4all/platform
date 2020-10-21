package org.smartbit4all.api.query;

import java.util.List;
import org.smartbit4all.api.query.bean.ExpressionClause;

public interface BeanQuery {

  <T> List<T> queryBean(ExpressionClause rootExpression, Class<T> clazz);
  
}
