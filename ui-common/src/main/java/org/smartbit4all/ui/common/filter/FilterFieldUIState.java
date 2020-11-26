package org.smartbit4all.ui.common.filter;

import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;

public class FilterFieldUIState extends AbstractUIState {

  private String labelCode;

  private FilterGroupUIState group;
  private DynamicFilter filter;
  private boolean isCloseable;

  private DynamicFilterLabelPosition position;

  public FilterFieldUIState(DynamicFilter filter, FilterGroupUIState group,
      DynamicFilterLabelPosition position) {
    super();
    this.group = group;
    applyFilterData(filter, position);
  }

  void applyFilterData(DynamicFilter filter, DynamicFilterLabelPosition position) {
    labelCode = filter.getMetaName();
    this.filter = filter;
    this.position = position;
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

  public DynamicFilterLabelPosition getPosition() {
    return position;
  }

}
