package org.smartbit4all.ui.common.filter;

import java.util.List;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOperation;

public class FilterFieldUIState extends AbstractUIState {

  private String labelCode;

  private DynamicFilter filter;
  private FilterGroupUIState group;
  private boolean isCloseable;

  private DynamicFilterLabelPosition position;

  private List<DynamicFilterOperation> operations;


  public FilterFieldUIState(DynamicFilter filter,
      FilterGroupUIState group,
      DynamicFilterLabelPosition position, boolean isCloseable,
      List<DynamicFilterOperation> operations) {
    super();
    this.filter = filter;
    this.group = group;
    this.labelCode = filter.getMetaName();
    this.position = position;
    this.isCloseable = isCloseable;
    this.operations = operations;
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

  public List<DynamicFilterOperation> getOperations() {
    return operations;
  }

  public DynamicFilterOperation getSelectedOperation() {
    return filter.getOperation();
  }

}
