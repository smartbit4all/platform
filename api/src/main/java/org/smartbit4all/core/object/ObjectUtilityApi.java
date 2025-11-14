package org.smartbit4all.core.object;

import java.util.List;

public interface ObjectUtilityApi {
  <E> List<E> asList(Class<E> clazz, List<?> value);
}
