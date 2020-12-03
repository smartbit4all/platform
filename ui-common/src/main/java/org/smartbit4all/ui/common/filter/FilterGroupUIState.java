package org.smartbit4all.ui.common.filter;

import org.smartbit4all.api.filter.bean.FilterField;
import org.smartbit4all.api.filter.bean.FilterGroup;
import org.smartbit4all.api.filter.bean.FilterGroupType;

public class FilterGroupUIState extends AbstractUIState {


  private FilterGroupType type;
  private String labelCode;
  private String iconCode;
  private String parentGroupId;
  private FilterGroupUIState parentGroup;
  private FilterGroup group;
  private boolean isCloseable;
  private boolean isRoot;
  private boolean isVisible;

  public FilterGroupUIState(FilterGroup group, FilterGroupUIState parentGroup,
      boolean isCloseable, boolean isVisible) {
    super();
    this.isCloseable = isCloseable;
    this.isVisible = isVisible;
    this.group = group;
    type = group.getType();
    labelCode = group.getName();
    iconCode = group.getIcon();
    if (parentGroup == null) {
      isRoot = true;
      parentGroupId = null;
      this.parentGroup = null;
    } else {
      isRoot = false;
      parentGroupId = parentGroup.getId();
      this.parentGroup = parentGroup;
    }
    // TODO closeable
  }

  public FilterGroupType getType() {
    return type;
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

  public void addDynamicFilter(FilterField filter) {
    group.addFilterFieldsItem(filter);
  }

  public boolean isVisible() {
    return isVisible;
  }

}
