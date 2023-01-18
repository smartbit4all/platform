package org.smartbit4all.domain.meta;

public class PropertyObject extends Property<Object> {

  private Property<?> basic;

  public PropertyObject(Property<?> basic) {
    super(basic.getName(), Object.class, null);
    this.basic = basic;
  }

}
