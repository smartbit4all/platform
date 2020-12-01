package org.smartbit4all.ui.common.filter;

import java.util.List;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.ui.common.view.UIView;

public interface DynamicFilterView extends UIView {

  void init();

  void renderFilterSelectors(List<FilterSelectorGroupUIState> filterSelectorGroups);

  void renderFilter(FilterFieldUIState filter, List<Value> possibleValues);

  void renderGroup(FilterGroupUIState group);

  void removeFilter(String filterId);

  void removeGroup(String groupId);

}
