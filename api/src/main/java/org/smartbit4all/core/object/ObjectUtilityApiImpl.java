package org.smartbit4all.core.object;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;



public class ObjectUtilityApiImpl implements ObjectUtilityApi {
  @Autowired
  private ObjectApi objectApi;

  @Override
  public <E> List<E> asList(Class<E> clazz, List<?> value) {
    return objectApi.asList(clazz, value);
  }


}
