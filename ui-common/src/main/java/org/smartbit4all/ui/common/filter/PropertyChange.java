package org.smartbit4all.ui.common.filter;

public class PropertyChange {

  private String property;

  private Object value;

  public PropertyChange(String property, Object value) {
    super();
    this.property = property;
    this.value = value;
  }

  public String getProperty() {
    return property;
  }

  public void setProperty(String property) {
    this.property = property;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}
