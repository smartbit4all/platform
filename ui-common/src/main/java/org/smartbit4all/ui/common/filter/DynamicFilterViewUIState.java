package org.smartbit4all.ui.common.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfig;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfigMode;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;

public class DynamicFilterViewUIState {

  List<FilterSelectorGroupUIState> filterSelectorGroups = new ArrayList<>();
  List<FilterSelectorUIState> filterSelectors = new ArrayList<>();
  FilterGroupUIState rootGroup;
  Map<String, FilterSelectorGroupUIState> filterSelectorGroupsById = new HashMap<>();
  Map<String, FilterSelectorUIState> filterSelectorsById = new HashMap<>();
  Map<String, FilterGroupUIState> groupsById = new HashMap<>();
  Map<String, FilterFieldUIState> filtersById = new HashMap<>();

  private DynamicFilterConfig filterConfig;
  private DynamicFilterConfigMode filterConfigMode;

  public DynamicFilterViewUIState(DynamicFilterConfigMode filterConfigMode,
      DynamicFilterGroup rootGroup) {
    this.rootGroup = new FilterGroupUIState(rootGroup, null);
    this.filterConfig = filterConfig;
    this.filterConfigMode = filterConfigMode;
  }

  void setFilterConfig(DynamicFilterConfig filterConfig) {
    this.filterConfig = filterConfig;
  }

  FilterSelectorGroupUIState getFilterSelectorGroupById(String id) {
    return filterSelectorGroupsById.get(id);
  }

  FilterSelectorUIState getFilterSelectorById(String id) {
    return filterSelectorsById.get(id);
  }

  FilterGroupUIState getFilterGroupById(String id) {
    return groupsById.get(id);
  }

  FilterFieldUIState getFilterById(String id) {
    return filtersById.get(id);
  }

}
