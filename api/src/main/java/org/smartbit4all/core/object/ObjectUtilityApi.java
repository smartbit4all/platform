package org.smartbit4all.core.object;

import java.util.List;

public interface ObjectUtilityApi {

  Integer addInt(Integer a, Integer b);

  String writeValueAsString(Object obj);

  <E> List<E> asList(Class<E> clazz, List<?> value);
}
