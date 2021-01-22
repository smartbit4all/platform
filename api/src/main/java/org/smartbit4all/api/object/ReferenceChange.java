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
 * The specific version of property change. In this case we have reference points to another object
 * that is part of the object hierarchy and managed.
 * 
 * @author Peter Boros
 */
public class ReferenceChange extends ChangeItem {

  /**
   * The change event of the reference.
   */
  private final ObjectChange changedReference;

  ReferenceChange(UUID parentId, String name, ObjectChange changedReference) {
    super(parentId, name);
    this.changedReference = changedReference;
  }

  /**
   * The change event of the reference.
   */
  public final ObjectChange getChangedReference() {
    return changedReference;
  }

}
