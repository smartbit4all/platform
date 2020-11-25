package org.smartbit4all.ui.common.filter;

import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupType;

public class FilterGroupUIState extends AbstractUIState {


  private DynamicFilterGroupType type;
  private String labelCode;
  private String iconCode;
  private String parentGroupId;
  private boolean isCloseable;

  public FilterGroupUIState(DynamicFilterGroup group, FilterGroupUIState parentGroup) {
    super();
    applyGroupData(group, parentGroup);
  }

  void applyGroupData(DynamicFilterGroup group, FilterGroupUIState parentGroup) {
    this.type = group.getType();
    this.labelCode = group.getName();
    this.iconCode = group.getIcon();
    this.parentGroupId = parentGroup == null ? null : parentGroup.getId();
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

}
