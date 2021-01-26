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

public class CollectionItemChange {

  /**
   * It identifies the order number of the collection item in the collection of the api object.
   */
  private final int orderNumber;

  /**
   * The object change of the collection item.
   */
  private final ObjectChange objectChange;

  CollectionItemChange(int orderNumber, ObjectChange objectChange) {
    super();
    this.orderNumber = orderNumber;
    this.objectChange = objectChange;
  }

  public final int getOrderNumber() {
    return orderNumber;
  }

  public final ObjectChange getObjectChange() {
    return objectChange;
  }

}