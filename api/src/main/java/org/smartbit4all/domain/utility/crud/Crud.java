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
package org.smartbit4all.domain.utility.crud;

import java.util.Map;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.service.CrudApis;
import org.smartbit4all.domain.service.modify.CreateInput;
import org.smartbit4all.domain.service.modify.CreateOutput;
import org.smartbit4all.domain.service.modify.DeleteInput;
import org.smartbit4all.domain.service.modify.DeleteOutput;
import org.smartbit4all.domain.service.modify.UpdateInput;
import org.smartbit4all.domain.service.modify.UpdateOutput;

/**
 * 
 * The static access to the basic data retrieval and modification functions. A simply to use
 * functional utility.
 * 
 * @author Peter Boros
 *
 */
public class Crud {

  /**
   * Private constructor to avoid accidental instantiation.
   */
  private Crud() {}

  /**
   * Instantiate a read builder object that can be used to define the read command.
   * 
   * @param <E> Based on an {@link EntityDefinition}.
   * @param entityDef {@link EntityDefinition} the read is going to be executed against.
   * @return The {@link CrudRead} builder object.
   */
  public static <E extends EntityDefinition> CrudRead<E> read(E entityDef) {
    return new CrudRead<>(entityDef);
  }

  /**
   * Execute an update immediately based on the tableData parameter.
   * 
   * @param <E>
   * @param tableData The table data defines the {@link EntityDefinition} and the update at the same
   *        time. All row is going to be updated based on the primary key that must be included as
   *        column in the table data.
   * @return The result that contains the number of rows affected.
   */
  public static <E extends EntityDefinition> UpdateOutput update(TableData<E> tableData) {
    if (tableData.isEmpty()) {
      return UpdateOutput.EMPTY;
    }
    return CrudApis.getCrudApi().executeUpdate(new UpdateInput<>(tableData));
  }

  /**
   * Execute a create immediately based on the tableData parameter.
   * 
   * @param <E>
   * @param tableData The table data defines the {@link EntityDefinition} and the insert at the same
   *        time. All row is going to be inserted based using the primary key that must be included
   *        as column in the table data.
   * @return The result that contains the number of rows affected.
   */
  public static <E extends EntityDefinition> CreateOutput create(TableData<E> tableData) {
    if (tableData.isEmpty()) {
      return CreateOutput.EMPTY;
    }

    return CrudApis.getCrudApi().executeCreate(new CreateInput<>(tableData));

  }

  /**
   * Execute a delete immediately based on the tableData parameter.
   * 
   * @param <E>
   * @param tableData The table data defines the {@link EntityDefinition} and the rows to delete at
   *        the same time. All row is going to be deleted based using the primary key that must be
   *        included as column in the table data. The rest of the table data is not used.
   * @return The result that contains the number of rows affected.
   */
  public static <E extends EntityDefinition> DeleteOutput delete(TableData<E> tableData) {
    if (tableData.isEmpty()) {
      return DeleteOutput.EMPTY;
    }

    return CrudApis.getCrudApi().executeDelete(new DeleteInput<>(tableData));

  }

  /**
   * This service ensure the caller that the defined records exist in the database after the call
   * and locked at the same time. It's optimized in a way that the insert for a unique column means
   * lock for this value.
   * 
   * @param <T> The type of the unique property.
   * @param entityDef The {@link EntityDefinition} for the operation.
   * @param uniqueProperty The unique property.
   * @param uniqueValue The value of the unique property.
   * @param sequenceName
   * @param extraValues The additional values in the unique record to set. It will be used only if
   *        there is a new record.
   * @return The table data of the existing record. It's already lock if we get back the result.
   */
  public static <E extends EntityDefinition, T> TableData<E> lockOrCreateAndLock(E entityDef,
      Property<T> uniqueProperty, T uniqueValue,
      String sequenceName, Map<Property<?>, Object> extraValues) throws Exception {
    return CrudApis.getCrudApi().lockOrCreateAndLock(entityDef, uniqueProperty, uniqueValue,
        sequenceName, extraValues);

  }

}
