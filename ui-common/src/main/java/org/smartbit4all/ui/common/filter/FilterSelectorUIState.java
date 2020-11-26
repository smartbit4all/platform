package org.smartbit4all.ui.common.filter;

import java.util.List;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterMeta;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOperation;

public class FilterSelectorUIState extends AbstractUIState {

  private String labelCode;
  private String name;
  private List<DynamicFilterOperation> operations;

  private FilterSelectorGroupUIState group;

  public FilterSelectorUIState(FilterSelectorGroupUIState group, DynamicFilterMeta filterMeta) {
    super();
    this.labelCode = filterMeta.getName();
    this.name = filterMeta.getName();
    this.operations = filterMeta.getOperations();
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

  public List<DynamicFilterOperation> getOperations() {
    return operations;
  }
}
