/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.service;

import org.smartbit4all.core.SB4Service;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.modify.Create;
import org.smartbit4all.domain.service.modify.Delete;
import org.smartbit4all.domain.service.modify.Update;
import org.smartbit4all.domain.service.query.Query;

/**
 * Return all the CRUD functions for an EntityDefinition.
 * 
 * @author Zoltan Suller
 */
public interface CrudService<E extends EntityDefinition> extends SB4Service {

  /**
   * The name of the service. We are using this for serialization.
   * 
   * @return
   */
  String name();

  /**
   * The {@link EntityDefinition} that is the root for all the CRUD functions.
   * 
   * @return
   */
  E entityDef();

  /**
   * An implementation of the Query function.
   * 
   * @return
   */
  Query<E> query();

  /**
   * An implementation of the Create function.
   * 
   * @return
   */
  Create<E> create();

  /**
   * An implementation of the Update function.
   * 
   * @return
   */
  Update<E> update();

  /**
   * An implementation of the Delete function.
   * 
   * @return
   */
  Delete<E> delete();

}
