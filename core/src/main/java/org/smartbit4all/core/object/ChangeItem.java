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
package org.smartbit4all.core.object;

import org.smartbit4all.core.utility.StringConstant;

/**
 * The super class of item change events of an object. They can be {@link PropertyChange},
 * {@link ReferenceChange} and {@link CollectionChange} instances.
 * 
 * @author Peter Boros
 *
 */
public abstract class ChangeItem {

  /**
   * The unique identifier of the object that embeds the given property, reference or collection.
   * It's the parent reference of these items. Inside an object we can see only our parent not the
   * grannies. If we no that we have grandparents then we have to subscribe for their references or
   * collections to have higher level of notification about them.
   */
  private final String parentPath;

  /**
   * The item name inside the given path. Properties, references and collections are also
   * represented as a name property inside an object.
   */
  protected final String name;

  /**
   * Constructing a new instance with the give parent URI.
   * 
   * @param uri
   */
  ChangeItem(String parentPath, String name) {
    super();
    this.parentPath = parentPath;
    this.name = name;
  }

  /**
   * The path of the API object that embeds the given property, reference or collection. It's the
   * parent reference of these items. Inside an object we can see only our parent not the grannies.
   * If we no that we have grandparents then we have to subscribe for their references or
   * collections to have higher level of notification about them.
   * 
   * @return
   */
  public final String getParentId() {
    return parentPath;
  }

  /**
   * The name of the given item. All the items inside an object are in the same name space so they
   * must have unique names inside an object.
   * 
   * @return
   */
  public final String getName() {
    return name;
  }

  public final String fullyQualifiedName() {
    return (parentPath == null || parentPath.isEmpty()) ? name
        : parentPath + StringConstant.SLASH + name;
  }

}