package org.smartbit4all.ui.common.filter2.model;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterGroupType;

public class FilterGroupSelectorModel {

  private String labelCode;
  private String iconCode;
  private FilterGroupType groupType;
  private List<FilterFieldSelectorModel> filters = new ArrayList<>();
  private Boolean closeable;
  private Boolean visible;

  private String currentGroupPath;

  public String getLabelCode() {
    return labelCode;
  }

  public void setLabelCode(String labelCode) {
    this.labelCode = labelCode;
  }

  public String getIconCode() {
    return iconCode;
  }

  public void setIconCode(String iconCode) {
    this.iconCode = iconCode;
  }

  public List<FilterFieldSelectorModel> getFilters() {
    return filters;
  }

  public void setFilters(List<FilterFieldSelectorModel> filters) {
    this.filters = filters;
  }

  public Boolean getCloseable() {
    return closeable;
  }

  public void setCloseable(Boolean closeable) {
    this.closeable = closeable;
  }

  public Boolean getVisible() {
    return visible;
  }

  public void setVisible(Boolean visible) {
    this.visible = visible;
  }

  public String getCurrentGroupPath() {
    return currentGroupPath;
  }

  public void setCurrentGroupPath(String currentGroupPath) {
    this.currentGroupPath = currentGroupPath;
  }

  public FilterGroupType getGroupType() {
    return groupType;
  }

  public void setGroupType(FilterGroupType groupType) {
    this.groupType = groupType;
  }

}
