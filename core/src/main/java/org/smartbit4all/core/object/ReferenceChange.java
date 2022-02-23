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

  ReferenceChange(String path, String name, ObjectChange changedReference) {
    super(path, name);
    this.changedReference = changedReference;
  }

  /**
   * The change event of the reference.
   */
  public final ObjectChange getChangedReference() {
    return changedReference;
  }

  @Override
  public String toString() {
    return name + StringConstant.COLON_SPACE + StringConstant.LEFT_CURLY + StringConstant.NEW_LINE
        + changedReference.toString() + StringConstant.NEW_LINE + StringConstant.RIGHT_CURLY;
  }

  public static ReferenceChange copyWithoutParent(ReferenceChange original, String parentPath) {
    String path = ObservableObjectHelper.removeParentPath(original.getPath(), parentPath);
    return new ReferenceChange(path, original.getName(),
        ObjectChange.copyWithoutParent(original.getChangedReference(), parentPath));
  }

}
