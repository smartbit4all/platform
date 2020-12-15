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
package org.smartbit4all.domain.utility.crud;

import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.modify.Create;
import org.smartbit4all.domain.service.modify.Delete;
import org.smartbit4all.domain.service.modify.Update;

public class Crud {

  public static <E extends EntityDefinition> CrudRead<E> read(E entityDef) {
    return new CrudRead<>(entityDef);
  }

  public static <E extends EntityDefinition> void update(TableData<E> tableData) throws Exception {
    @SuppressWarnings("unchecked")
    Update<E> update = (Update<E>) tableData.entity().services().crud().update();
    update.values(tableData).execute();
  }

  public static <E extends EntityDefinition> void create(TableData<E> tableData) throws Exception {
    @SuppressWarnings("unchecked")
    Create<E> create = (Create<E>) tableData.entity().services().crud().create();
    create.values(tableData).execute();
  }

  public static <E extends EntityDefinition> void delete(TableData<E> tableData) throws Exception {
    @SuppressWarnings("unchecked")
    Delete<E> delete = (Delete<E>) tableData.entity().services().crud().delete();
    delete.by(tableData).execute();
  }

}
