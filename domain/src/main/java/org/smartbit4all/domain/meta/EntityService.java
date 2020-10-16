package org.smartbit4all.domain.meta;

import java.util.Map;
import org.smartbit4all.core.SB4Service;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.service.CrudService;

public interface EntityService<E extends EntityDefinition> extends SB4Service {

  CrudService<E> crud();

  /**
   * This service ensure the caller that the defined records exist in the database after the call
   * and locked at the same time. It's optimized in a way that the insert for a unique column means
   * lock for this value.
   * 
   * @param <T> The type of the unique property.
   * @param uniqueProperty The unique property.
   * @param uniqueValue The value of the unique property.
   * @param idService The identifier service for the function.
   * @param extraValues The additional values in the unique record to set. It will be used only if
   *        there is a new record.
   * @return The table data of the existing record. It's already lock if we get back the result.
   */
  <T> TableData<E> lockOrCreateAndLock(Property<T> uniqueProperty, T uniqueValue,
      String sequenceName, Map<Property<?>, Object> extraValues) throws Exception;

}
