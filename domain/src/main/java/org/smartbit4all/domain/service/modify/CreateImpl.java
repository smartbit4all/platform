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
package org.smartbit4all.domain.service.modify;

import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

public abstract class CreateImpl<E extends EntityDefinition>
    extends SB4FunctionImpl<CreateInput<E>, CreateOutput<E>> implements Create<E> {

  /**
   * The {@link EntityDefinition} that we running on.
   */
  protected E entityDef;

  @Override
  public Create<E> values(TableData<E> data) {
    this.input = new CreateInput<>(data);
    return this;
  }

}
