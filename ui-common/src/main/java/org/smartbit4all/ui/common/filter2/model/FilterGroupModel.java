package org.smartbit4all.ui.common.filter2.model;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterGroupType;

public class FilterGroupModel {

  private FilterGroupType groupType = FilterGroupType.AND;
  private FilterGroupLabel label;
  private Boolean closeable = Boolean.FALSE;
  private Boolean root = Boolean.FALSE;
  private Boolean visible = Boolean.TRUE;
  private Boolean active = Boolean.FALSE;
  private Boolean childGroupAllowed = Boolean.FALSE;
  private Boolean groupTypeChangeEnabled = Boolean.FALSE;
  private final List<FilterGroupModel> groups = new ArrayList<>();
  private final List<FilterFieldModel> filters = new ArrayList<>();

  public FilterGroupType getGroupType() {
    return groupType;
  }

  public void setGroupType(FilterGroupType groupType) {
    this.groupType = groupType;
  }

  public FilterGroupLabel getLabel() {
    return label;
  }

  public void setLabel(FilterGroupLabel label) {
    this.label = label;
  }

  public Boolean getCloseable() {
    return closeable;
  }

  public void setCloseable(Boolean closeable) {
    this.closeable = closeable;
  }

  public Boolean getRoot() {
    return root;
  }

  public void setRoot(Boolean root) {
    this.root = root;
  }

  public Boolean getVisible() {
    return visible;
  }

  public void setVisible(Boolean visible) {
    this.visible = visible;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Boolean getChildGroupAllowed() {
    return childGroupAllowed;
  }

  public void setChildGroupAllowed(Boolean childGroupAllowed) {
    this.childGroupAllowed = childGroupAllowed;
  }

  public Boolean getGroupTypeChangeEnabled() {
    return groupTypeChangeEnabled;
  }

  public void setGroupTypeChangeEnabled(Boolean groupTypeChangeEnabled) {
    this.groupTypeChangeEnabled = groupTypeChangeEnabled;
  }

  public List<FilterGroupModel> getGroups() {
    return groups;
  }

  public List<FilterFieldModel> getFilters() {
    return filters;
  }

}
