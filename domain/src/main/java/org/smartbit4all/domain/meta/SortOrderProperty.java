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
package org.smartbit4all.domain.meta;

/**
 * The sort order for a given property.
 * 
 * @author Peter Boros
 */
public class SortOrderProperty {

  /**
   * The property the sorting as based on.
   */
  public Property<?> property;

  /**
   * If true then the sorting is ascending. If false then descending by the value of the property.
   */
  public boolean asc;

  /**
   * If true then the null values come at the beginning of the ordered list. If <b>false
   * (default)</b> then the null values are at the end of the list.
   */
  public boolean nullsFirst = false;

  SortOrderProperty(Property<?> property, boolean asc) {
    super();
    this.property = property;
    this.asc = asc;
  }

  /**
   * Set ascending for the order.
   * 
   * @return Fluid API
   */
  public SortOrderProperty asc() {
    asc = true;
    return this;
  }

  /**
   * Set descending for the order.
   * 
   * @return Fluid API
   */
  public SortOrderProperty desc() {
    asc = false;
    return this;
  }

  /**
   * Set nulls first flag for the order.
   * 
   * @return Fluid API
   */
  public SortOrderProperty nullsFirst() {
    nullsFirst = true;
    return this;
  }

  /**
   * Set nulls first flag for the order.
   * 
   * @return Fluid API
   */
  public SortOrderProperty nullsLast() {
    nullsFirst = false;
    return this;
  }

}
