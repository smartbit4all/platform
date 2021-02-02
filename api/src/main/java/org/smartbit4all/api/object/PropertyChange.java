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

import org.smartbit4all.core.utility.StringConstant;

/**
 * The property change event for the property of an instance. The property change can be primitive
 * type but can be a reference for another object.
 * 
 * @author Peter Boros
 */
public class PropertyChange extends ChangeItem {

  /**
   * The old value of the given property. The old value can be null if we have no idea what was the
   * original value.
   */
  private final Object oldValue;

  /**
   * The new value of the property is mandatory.
   */
  private Object newValue;

  PropertyChange(String parentPath, String name, Object oldValue, Object newValue) {
    super(parentPath, name);
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  /**
   * The old value of the given property. It's optional because we might have no idea about the old
   * value.
   */
  public final Object getOldValue() {
    return oldValue;
  }

  /**
   * The new value of the property that is mandatory.
   */
  public final Object getNewValue() {
    return newValue;
  }

  public final void setNewValue(Object newValue) {
    this.newValue = newValue;
  }

  @Override
  public String toString() {
    return name + StringConstant.COLON_SPACE + StringConstant.LEFT_PARENTHESIS + oldValue
        + StringConstant.ARROW + newValue + StringConstant.RIGHT_PARENTHESIS;
  }

}
