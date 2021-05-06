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

import org.smartbit4all.core.event.EventSubscription;
import org.smartbit4all.core.utility.StringConstant;

public class PropertyChangeSubscription extends EventSubscription<PropertyChange> {

  private String parentPath;

  private String propertyName;

  /**
   * The expected type of the property change. The value will be converted
   */
  private Class<?> expectedType;

  public PropertyChangeSubscription property(String parentPath, String propertyName) {
    this.parentPath = parentPath;
    this.propertyName = propertyName;
    return this;
  }

  public PropertyChangeSubscription as(Class<?> expectedType) {
    this.expectedType = expectedType;
    return this;
  }

  public final String getParentPath() {
    return parentPath;
  }

  public final void setParentPath(String parentPath) {
    this.parentPath = parentPath;
  }

  public final String getPropertyName() {
    return propertyName;
  }

  public final void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }
  
  public final String fullyQualifiedName() {
    return (parentPath == null || parentPath.isEmpty()) ? propertyName
        : parentPath + StringConstant.SLASH + propertyName;
  }

}
