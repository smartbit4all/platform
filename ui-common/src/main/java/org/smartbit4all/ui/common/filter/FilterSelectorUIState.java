package org.smartbit4all.ui.common.filter;

import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterMeta;

public class FilterSelectorUIState extends AbstractUIState {

  private String labelCode;
  private String name;

  private FilterSelectorGroupUIState group;

  public FilterSelectorUIState(FilterSelectorGroupUIState group, DynamicFilterMeta filterMeta) {
    super();
    applyDataFrom(group, filterMeta);
  }

  void applyDataFrom(FilterSelectorGroupUIState group, DynamicFilterMeta filterMeta) {
    this.labelCode = filterMeta.getName();
    this.name = filterMeta.getName();
    this.group = group;
  }

  public String getLabelCode() {
    return labelCode;
  }

  public String getName() {
    return name;
  }

  public FilterSelectorGroupUIState getGroup() {
    return group;
  }
}
