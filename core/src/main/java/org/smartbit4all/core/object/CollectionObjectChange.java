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

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The change of a collection with changed instances inside.
 * 
 * @author Attila Mate
 */
public class CollectionObjectChange extends ChangeItem {

  private static final String ITEM = "item";
  private static final String COLLECTION = "collection";

  /**
   * The instance changes for the given collection. New and deleted items will be included.
   */
  private final List<ObjectChangeSimple> changes = new ArrayList<>();

  CollectionObjectChange(String parentPath, String name) {
    super(parentPath, name);
  }

  /**
   * The instance changes of the given collection change.
   * 
   * @return
   */
  public final List<ObjectChangeSimple> getChanges() {
    return changes;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    sb.append(StringConstant.DOT);
    sb.append(COLLECTION);
    sb.append(StringConstant.COLON);
    changes.forEach(c -> {
      sb.append(StringConstant.NEW_LINE).append(name).append(StringConstant.DOT).append(ITEM)
          .append(StringConstant.SPACE_HYPHEN_SPACE).append(StringConstant.LEFT_CURLY)
          .append(StringConstant.NEW_LINE);
      sb.append(c.toString()).append(StringConstant.NEW_LINE);
      sb.append(StringConstant.RIGHT_CURLY);
    });
    return sb.toString();
  }

}
