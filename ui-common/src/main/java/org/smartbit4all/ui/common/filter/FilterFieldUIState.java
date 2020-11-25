package org.smartbit4all.ui.common.filter;

import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;

public class FilterFieldUIState extends AbstractUIState {

  private String labelCode;

  private FilterGroupUIState group;
  private boolean isCloseable;

  public FilterFieldUIState(DynamicFilter filter) {
    super();
    applyFilterData(filter);
  }

  void applyFilterData(DynamicFilter filter) {
    labelCode = filter.getMetaName();
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

}
