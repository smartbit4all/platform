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
package org.smartbit4all.api.object;

import java.util.UUID;

/**
 * The property change event for the property of an instance. The property change can be primitive
 * type but can be a reference for another object.
 * 
 * @author Peter Boros
 */
public class PropertyChange<P> extends ChangeItem {

  /**
   * The old value of the given property. The old value can be null if we have no idea what was the
   * original value.
   */
  private final P oldValue;

  /**
   * The new value of the property is mandatory.
   */
  private final P newValue;

  PropertyChange(UUID parentId, String name, P oldValue, P newValue) {
    super(parentId, name);
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  /**
   * The old value of the given property. It's optional because we might have no idea about the old
   * value.
   */
  public final P getOldValue() {
    return oldValue;
  }

  /**
   * The new value of the property that is mandatory.
   */
  public final P getNewValue() {
    return newValue;
  }

}
