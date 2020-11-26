package org.smartbit4all.ui.common.filter;

import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;

public class FilterFieldUIState extends AbstractUIState {

  private String labelCode;

  private FilterGroupUIState group;
  private DynamicFilter filter;
  private boolean isCloseable;

  public FilterFieldUIState(DynamicFilter filter, FilterGroupUIState group) {
    super();
    this.group = group;
    applyFilterData(filter);
  }

  void applyFilterData(DynamicFilter filter) {
    labelCode = filter.getMetaName();
    this.filter = filter;
    // TODO set isCloseable
  }

  public String getLabelCode() {
    return labelCode;
  }

  public Object getGroupId() {
    return group.getId();
  }

  public boolean isCloseable() {
    return isCloseable;
  }

  public FilterGroupUIState getGroup() {
    return group;
  }

  public DynamicFilter getFilter() {
    return filter;
  }

}
