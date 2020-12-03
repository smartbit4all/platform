package org.smartbit4all.ui.common.filter;

import java.util.List;
import org.smartbit4all.api.filter.bean.FilterField;
import org.smartbit4all.api.filter.bean.FilterOperation;

public class FilterFieldUIState extends AbstractUIState {

  private String labelCode;

  private FilterField filter;
  private FilterGroupUIState group;
  private boolean isCloseable;

  private FilterLabelPosition position;

  private List<FilterOperation> operations;


  public FilterFieldUIState(FilterField filter,
      FilterGroupUIState group,
      FilterLabelPosition position, boolean isCloseable,
      List<FilterOperation> operations) {
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

  public FilterField getFilter() {
    return filter;
  }

  public FilterLabelPosition getPosition() {
    return position;
  }

  public List<FilterOperation> getOperations() {
    return operations;
  }

  public FilterOperation getSelectedOperation() {
    return filter.getOperation();
  }

}
