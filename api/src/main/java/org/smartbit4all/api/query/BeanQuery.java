package org.smartbit4all.api.query;

public interface BeanQuery {

  <T> T queryBean(ExpressionClause rootExpression, Class<T> clazz);
  
}
