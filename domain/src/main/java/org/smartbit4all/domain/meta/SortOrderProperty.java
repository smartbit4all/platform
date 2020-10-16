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
