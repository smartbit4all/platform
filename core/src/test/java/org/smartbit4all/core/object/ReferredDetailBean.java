package org.smartbit4all.core.object;

public class ReferredDetailBean {

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ReferredDetailBean name(String name) {
    this.name = name;
    return this;
  }

  @Override
  public String toString() {
    return "(refDetailName: " + name + ")";
  }
}
