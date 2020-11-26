package org.smartbit4all.ui.common.filter;

import java.util.List;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfig;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOperation;
import org.smartbit4all.ui.common.view.UIView;

public interface DynamicFilterView extends UIView {

  void init();

  void renderFilterSelectors(List<FilterSelectorGroupUIState> filterSelectorGroups);

  void renderFilterSelectors(DynamicFilterConfig filterConfig);

  void renderFilter(FilterFieldUIState filter);

  void renderFilter(String groupId, String filterId, DynamicFilter dynamicFilter,
      List<DynamicFilterOperation> filterOptions, boolean isClosable);

  void renderFilter(String filterId, DynamicFilter dynamicFilter);

  void renderGroup(FilterGroupUIState group);

  void renderGroup(String parentGroupId, String groupId, DynamicFilterGroup group,
      boolean isClosable);

  void removeFilter(String filterId);

  void removeGroup(String groupId);

}
