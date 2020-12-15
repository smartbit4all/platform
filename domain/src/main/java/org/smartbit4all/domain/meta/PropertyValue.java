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

/**
 * The property value is base value access method for a given {@link Property}. It's used as
 * implementation for the {@link InputValue}, {@link OutputValue} and {@link InOutValue} for
 * example. The basic idea behind to hide how to access the value of a {@link Property}. If we write
 * a logic then we can refer to a {@link PropertyValue} without knowing where the value comes from.
 * 
 * It can help us to run logic in test scenario without having "real" data. We add all the inputs
 * and let the logic run and check if the outputs match the requirements.
 * 
 * @author Peter Boros
 * @param <T>
 */
class PropertyValue<T> implements InOutValue<T> {

  /**
   * The value itself.
   */
  private T value;

  /**
   * The meta property.
   */
  protected Property<T> property;

  public PropertyValue(Property<T> property) {
    super();
    this.property = property;
  }

  /**
   * The meta property.
   * 
   * @return
   */
  public Property<T> property() {
    return property;
  }

  @Override
  public T get() {
    return value;
  }

  @Override
  public void set(T value) {
    this.value = value;
  }

}
