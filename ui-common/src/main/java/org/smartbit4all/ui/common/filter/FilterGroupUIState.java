package org.smartbit4all.ui.common.filter;

import org.smartbit4all.api.filter.bean.FilterField;
import org.smartbit4all.api.filter.bean.FilterGroup;
import org.smartbit4all.api.filter.bean.FilterGroupType;

public class FilterGroupUIState extends AbstractUIState {


  private FilterGroupType groupType;
  private String labelCode;
  private String iconCode;
  private String parentGroupId;
  private FilterGroupUIState parentGroup;
  private FilterGroup group;
  private boolean isCloseable;
  private boolean isRoot;
  private boolean isVisible;
  private boolean isActive;
  private boolean isChildGroupAllowed;
  private boolean isGroupTypeChangeEnabled;

  public FilterGroupUIState(FilterGroup group, FilterGroupUIState parentGroup,
      String iconCode, boolean isCloseable, boolean isVisible, boolean isChildGroupAllowed,
      boolean isGroupTypeChangeEnabled) {
    super();
    this.isCloseable = isCloseable;
    this.isVisible = isVisible;
    this.group = group;
    this.groupType = group.getType();
    this.labelCode = group.getName();
    this.iconCode = iconCode;
    this.isChildGroupAllowed = isChildGroupAllowed;
    this.isGroupTypeChangeEnabled = isGroupTypeChangeEnabled;
    if (parentGroup == null) {
      isRoot = true;
      parentGroupId = null;
      this.parentGroup = null;
    } else {
      isRoot = false;
      parentGroupId = parentGroup.getId();
      this.parentGroup = parentGroup;
    }
  }

  public FilterGroupType getGroupType() {
    return groupType;
  }

  public String getLabelCode() {
    return labelCode;
  }

  public String getIconCode() {
    return iconCode;
  }

  public String getParentGroupId() {
    return parentGroupId;
  }

  public boolean isCloseable() {
    return isCloseable;
  }

  public boolean isRoot() {
    return isRoot;
  }

  public FilterGroupUIState getParentGroup() {
    return parentGroup;
  }

  public void addFilterField(FilterField filterField) {
    group.addFilterFieldsItem(filterField);
  }

  public void addFilterGroup(FilterGroup filterGroup) {
    group.addFilterGroupsItem(filterGroup);
  }

  public void removeFilterField(FilterField filterField) {
    group.getFilterFields().remove(filterField);
  }

  public void removeFilterGroup(FilterGroup filterGroup) {
    group.getFilterGroups().remove(filterGroup);
  }

  public void clearChildren() {
    if (group.getFilterFields() != null) {
      group.getFilterFields().clear();
    }
    if (group.getFilterGroups() != null) {
      group.getFilterGroups().clear();
    }
  }

  public boolean isVisible() {
    return isVisible;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  public boolean isActive() {
    return isActive;
  }

  public boolean isChildGroupAllowed() {
    return isChildGroupAllowed;
  }

  public boolean isGroupTypeChangeEnabled() {
    return isGroupTypeChangeEnabled;
  }

}
