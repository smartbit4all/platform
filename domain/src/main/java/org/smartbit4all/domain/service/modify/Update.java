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
 * The update operation on an entity. It always have a list of records with primary keys to identify
 * them in the data source (like RDBMS). It updates the already existing records.
 * 
 * @author Peter Boros
 */
public interface Update<E extends EntityDefinition>
    extends SB4Function<UpdateInput<E>, UpdateOutput> {

  // TODO into should be TableData result. Maybe result? like updateWithRefresh

  /**
   * Set the source of the creation. It provides the records for the creation.
   * 
   * @param source
   * @return
   */
  Update<E> values(TableData<E> data);

}
