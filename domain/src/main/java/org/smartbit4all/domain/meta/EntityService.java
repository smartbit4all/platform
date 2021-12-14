/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
   * @param sequenceName
   * @param extraValues The additional values in the unique record to set. It will be used only if
   *        there is a new record.
   * @return The table data of the existing record. It's already lock if we get back the result.
   */
  <T> TableData<E> lockOrCreateAndLock(Property<T> uniqueProperty, T uniqueValue,
      String sequenceName, Map<Property<?>, Object> extraValues) throws Exception;

}
