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
 * The output value is hiding the knowledge of how to set the property for a given computation.
 * 
 * @author Peter Boros
 * @param <T> The type of the value.
 */
public interface OutputValue<T> {

  /**
   * The computation can set the value of the given property.
   * 
   * @param value
   */
  public abstract void set(T value);

  /**
   * Defines the property the value comes from.
   * 
   * @return
   */
  Property<T> property();

}
