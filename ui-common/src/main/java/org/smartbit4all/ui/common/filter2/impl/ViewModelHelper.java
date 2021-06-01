package org.smartbit4all.ui.common.filter2.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.filter.model.DynamicFilterModel;
import org.smartbit4all.ui.api.filter.model.FilterFieldLabel;
import org.smartbit4all.ui.api.filter.model.FilterFieldModel;
import org.smartbit4all.ui.api.filter.model.FilterFieldSelectorModel;
import org.smartbit4all.ui.api.filter.model.FilterGroupLabel;
import org.smartbit4all.ui.api.filter.model.FilterGroupModel;
import org.smartbit4all.ui.api.filter.model.FilterGroupSelectorModel;

public class ViewModelHelper {

  private static Map<Class<?>, ApiBeanDescriptor> filterDescriptors;

  static {
    Set<Class<?>> beans = new HashSet<>();
    beans.add(DynamicFilterModel.class);
    beans.add(FilterGroupSelectorModel.class);
    beans.add(FilterFieldSelectorModel.class);
    beans.add(FilterGroupModel.class);
    beans.add(FilterGroupLabel.class);
    beans.add(FilterFieldModel.class);
    beans.add(FilterFieldLabel.class);
    beans.add(FilterOperation.class);
    beans.add(FilterOperandValue.class);
    beans.add(Value.class);
    filterDescriptors = ApiBeanDescriptor.of(beans);

  }

  private ViewModelHelper() {}

  static Map<Class<?>, ApiBeanDescriptor> getFilterDescriptors() {
    return filterDescriptors;
  }

}
