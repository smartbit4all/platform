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

/**
 * The change state of the {@link ObjectChange} events. The state express the changes inside the
 * stateful api. If we start editing an existing Customer object then we will be notified about it
 * as new. Because it's a newly referred object in our object hierarchy.
 * 
 * @author Peter Boros
 */
public enum ChangeState {

  /**
   * The new means that the object is referred as new in the stateful api. The object itself can be
   * a new one or an existing one in the repository.
   */
  NEW,
  /**
   * The modified means that we have embedded modifications also.
   */
  MODIFIED,
  /**
   * The reference is deleted from the object hierarchy so we can remove the whole subtree.
   */
  DELETED,
  /**
   * Currently the item is unchanged.
   */
  NOP

}
