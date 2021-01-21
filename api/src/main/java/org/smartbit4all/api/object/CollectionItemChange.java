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
