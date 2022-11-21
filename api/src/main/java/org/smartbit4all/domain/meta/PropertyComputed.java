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

import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterHelper;

/**
 * This property is computed by an algorithm represented by a {@link ComputationLogic}. For the
 * computation there are other necessary properties to include.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public class PropertyComputed<T> extends Property<T> {

  /**
   * The computation logic definition responsible for computing the value of this property. If this
   * property is added to a data representation then the logic will be added automatically. The
   * logic itself is always instantiated at the given place. So the same computation logic can have
   * more then one implementation depending on the context that we currently have.
   */
  private Class<? extends ComputationLogic> logic;

  /**
   * Constructs a computed property.
   * 
   * @param name
   * @param type
   * @param logic
   */
  public PropertyComputed(String name, Class<T> type, Class<? extends ComputationLogic> logic) {
    this(name, type, null, logic);
  }

  public PropertyComputed(String name, Class<T> type, JDBCDataConverter<T, ?> jdbcConverter,
      Class<? extends ComputationLogic> logic) {
    super(name, type, jdbcConverter);
    this.logic = logic;
  }

  /**
   * The interface class of the logic that calculates the given property.
   * 
   * @return
   */
  public Class<? extends ComputationLogic> getLogic() {
    return logic;
  }

  public static <T> PropertyComputed<T> create(String name, Class<T> type,
      JDBCDataConverterHelper jdbcDataConverterHelper, Class<? extends ComputationLogic> logic) {
    return new PropertyComputed<T>(name, type, jdbcDataConverterHelper.from(type), logic);
  }
}
