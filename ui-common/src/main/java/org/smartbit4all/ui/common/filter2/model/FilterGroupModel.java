package org.smartbit4all.ui.common.filter2.model;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterGroupType;

public class FilterGroupModel {

  private FilterGroupType groupType = FilterGroupType.AND;
  private FilterGroupLabel label;
  private boolean closeable;
  private boolean root;
  private boolean visible;
  private boolean active;
  private boolean childGroupAllowed;
  private boolean groupTypeChangeEnabled;
  private final List<FilterGroupModel> filterGroupModels = new ArrayList<>();
  private final List<FilterFieldModel> filterFieldModels = new ArrayList<>();

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

  public boolean isCloseable() {
    return closeable;
  }

  public void setCloseable(boolean closeable) {
    this.closeable = closeable;
  }

  public boolean isRoot() {
    return root;
  }

  public void setRoot(boolean root) {
    this.root = root;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isChildGroupAllowed() {
    return childGroupAllowed;
  }

  public void setChildGroupAllowed(boolean childGroupAllowed) {
    this.childGroupAllowed = childGroupAllowed;
  }

  public boolean isGroupTypeChangeEnabled() {
    return groupTypeChangeEnabled;
  }

  public void setGroupTypeChangeEnabled(boolean groupTypeChangeEnabled) {
    this.groupTypeChangeEnabled = groupTypeChangeEnabled;
  }

  public List<FilterGroupModel> getFilterGroupModels() {
    return filterGroupModels;
  }

  public List<FilterFieldModel> getFilterFieldModels() {
    return filterFieldModels;
  }

}
