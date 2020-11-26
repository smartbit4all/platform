package org.smartbit4all.ui.common.filter;

import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupType;

public class FilterGroupUIState extends AbstractUIState {


  private DynamicFilterGroupType type;
  private String labelCode;
  private String iconCode;
  private String parentGroupId;
  private FilterGroupUIState parentGroup;
  private DynamicFilterGroup group;
  private boolean isCloseable;
  private boolean isRoot;

  public FilterGroupUIState(DynamicFilterGroup group, FilterGroupUIState parentGroup,
      boolean isCloseable) {
    super();
    this.isCloseable = isCloseable;
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

  public DynamicFilterGroupType getType() {
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

  public void addDynamicFilter(DynamicFilter filter) {
    group.addFiltersItem(filter);
  }
}
