package org.smartbit4all.ui.common.filter2.model;

import java.util.List;
import org.smartbit4all.api.filter.bean.FilterOperation;

public class FilterFieldSelectorModel {

  private String labelCode;
  private String iconCode;
  private String style;

  private List<FilterOperation> operations;

  private Boolean enabled;

  public String getLabelCode() {
    return labelCode;
  }

  public void setLabelCode(String labelCode) {
    this.labelCode = labelCode;
  }

  public List<FilterOperation> getOperations() {
    return operations;
  }

  public void setOperations(List<FilterOperation> operations) {
    this.operations = operations;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getIconCode() {
    return iconCode;
  }

  public void setIconCode(String iconCode) {
    this.iconCode = iconCode;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

}
