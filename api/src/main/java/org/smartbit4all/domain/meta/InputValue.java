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
 * The input value is hiding the knowledge of how to access the input property for a given
 * computation.
 * 
 * TODO The toString must be implemented to be able to use the String value without get()!
 * 
 * @author Peter Boros
 * @param <T> The type of the value.
 */
public interface InputValue<T> {

  /**
   * Retrieves the value of the dependent property when the computation is running
   * 
   * @return
   */
  public abstract T get();

  @Override
  String toString();

  /**
   * Defines the property the value comes from.
   * 
   * @return
   */
  Property<T> property();

}
