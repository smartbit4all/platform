package org.smartbit4all.core.json;

public class ContainedBean {

  private String detailString;

  private ContainedBeanType type;

  final String getDetailString() {
    return detailString;
  }

  final void setDetailString(String detailString) {
    this.detailString = detailString;
  }

  final ContainedBeanType getType() {
    return type;
  }

  final void setType(ContainedBeanType type) {
    this.type = type;
  }

}
