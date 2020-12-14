package org.smartbit4all.ui.common.filter;

import java.util.List;
import org.smartbit4all.ui.common.view.UIView;

public interface DynamicFilterView extends UIView {

  void init();

  void renderFilterSelectors(List<FilterSelectorGroupUIState> filterSelectorGroups);

  void updateFilterSelector(FilterSelectorUIState filterSelector);

  void renderFilter(FilterFieldUIState filter);

  void renderGroup(FilterGroupUIState group);

  void removeFilter(String filterId);

  void removeGroup(String groupId);

  void changeActiveGroup(FilterGroupUIState prevGroupState, FilterGroupUIState newGroupState);

  void moveFilter(String groupId, String filterId);

}
