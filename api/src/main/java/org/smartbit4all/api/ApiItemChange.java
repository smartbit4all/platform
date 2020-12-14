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
package org.smartbit4all.api;

/**
 * The change refers the old value and the new value. For an api it's optional to provide this level
 * of information about the modification of an object but this can be important in some cases.
 * 
 * @author Peter Boros
 */
public class ApiItemChange {

  /**
   * The old value of the given change.
   */
  private final Object oldValue;

  /**
   * The new value.
   */
  private final Object newValue;

  public ApiItemChange(Object oldValue, Object newValue) {
    super();
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

}
