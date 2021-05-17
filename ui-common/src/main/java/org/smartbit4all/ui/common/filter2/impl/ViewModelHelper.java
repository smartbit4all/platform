package org.smartbit4all.ui.common.filter2.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.common.filter2.model.FilterFieldModel;
import org.smartbit4all.ui.common.filter2.model.FilterLabel;

public class ViewModelHelper {

  private static Map<Class<?>, ApiBeanDescriptor> filterDescriptors;

  static {
    Set<Class<?>> beans = new HashSet<>();
    beans.add(FilterFieldModel.class);
    beans.add(FilterLabel.class);
    beans.add(FilterOperation.class);
    beans.add(FilterOperandValue.class);
    filterDescriptors = ApiBeanDescriptor.of(beans);

  }

  private ViewModelHelper() {}

  static Map<Class<?>, ApiBeanDescriptor> getFilterDescriptors() {
    return filterDescriptors;
  }

}
