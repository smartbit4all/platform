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
package org.smartbit4all.domain.meta;

import java.lang.reflect.Method;

/**
 * The method returns the {@link Reference} of a {@link EntityDefinition}.
 * 
 * @author Peter Boros
 */
class EDMReference extends EntityDefinitionMethod {

  /**
   * The referenced entity.
   */
  EntityDefinition referencedEntity;

  /**
   * The reference for the given entity.
   */
  Reference<?, ?> reference;

  public EDMReference(EntityDefinition referencedEntity, Reference<?, ?> reference) {
    super();
    this.referencedEntity = referencedEntity;
    this.reference = reference;
  }

  @Override
  Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return reference;
  }

}
