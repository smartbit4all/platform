package org.smartbit4all.ui.common.filter;

import org.smartbit4all.api.filter.bean.FilterOperandValue;

public interface FilterValueChangeListener {

  void filterValueChanged(String filterId, FilterOperandValue value1, FilterOperandValue value2,
      FilterOperandValue value3);

}
