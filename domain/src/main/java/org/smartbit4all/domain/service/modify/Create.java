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
package org.smartbit4all.domain.service.modify;

import org.smartbit4all.core.SB4Function;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

/**
 * The create is a service responsible for creating new instances with the given values.
 * 
 * @author Peter Boros
 */
public interface Create<E extends EntityDefinition>
    extends SB4Function<CreateInput<E>, CreateOutput> {

  // TODO into should be TableData result. Maybe result? like insertWithRefresh

  /**
   * Set the values based on a {@link TableData}.
   * 
   * @param tableData The table data with the values for the create (insert) statement.
   * @return Fluid API
   * 
   */
  Create<E> values(TableData<E> data);

  // * Set the values based on a collection of beans. The beans will be converted to {@link
  // TableData}
  // * in the background.
  // *
  // * @param <B>
  // * @param beans The collection of beans.
  // * @return Fluid API
  // */
  // <B> Create values(Collection<B> beans);

}
