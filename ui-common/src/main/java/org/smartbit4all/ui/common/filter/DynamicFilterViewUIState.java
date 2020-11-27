package org.smartbit4all.ui.common.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfig;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfigMode;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupMeta;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterMeta;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOperation;

public class DynamicFilterViewUIState {

  List<FilterSelectorGroupUIState> filterSelectorGroups = new ArrayList<>();
  List<FilterSelectorUIState> filterSelectors = new ArrayList<>();
  FilterGroupUIState rootGroup;
  Map<String, FilterSelectorGroupUIState> filterSelectorGroupsById = new HashMap<>();
  Map<String, FilterSelectorUIState> filterSelectorsById = new HashMap<>();
  Map<String, FilterGroupUIState> groupUIStatesById = new HashMap<>();
  Map<String, FilterFieldUIState> filterUIStatesById = new HashMap<>();
  Map<String, DynamicFilterGroup> groupsById = new HashMap<>();
  Map<String, DynamicFilter> filtersById = new HashMap<>();
  FilterGroupUIState activeGroup;

  private DynamicFilterConfig filterConfig;
  private DynamicFilterConfigMode filterConfigMode;

  public DynamicFilterViewUIState(DynamicFilterConfigMode filterConfigMode,
      DynamicFilterGroup rootGroup) {
    this.rootGroup = new FilterGroupUIState(rootGroup, null, false);
    this.activeGroup = this.rootGroup;
    groupUIStatesById.put(this.rootGroup.getId(), this.rootGroup);
    this.filterConfigMode = filterConfigMode;
    if (this.filterConfigMode == null) {
      this.filterConfigMode = DynamicFilterConfigMode.SIMPLE_DYNAMIC;
    }
  }

  void setFilterConfig(DynamicFilterConfig filterConfig) {
    this.filterConfig = filterConfig;
    Map<String, FilterSelectorGroupUIState> groupsByName = new HashMap<>();
    for (DynamicFilterGroupMeta group : this.filterConfig.getDynamicFilterGroupMetas()) {
      FilterSelectorGroupUIState groupUIState = new FilterSelectorGroupUIState(group);
      filterSelectorGroups.add(groupUIState);
      filterSelectorGroupsById.put(groupUIState.getId(), groupUIState);
      groupsByName.put(group.getName(), groupUIState);
    }
    for (DynamicFilterMeta field : this.filterConfig.getDynamicFilterMetas()) {
      FilterSelectorGroupUIState group = groupsByName.get(field.getGroupName());
      FilterSelectorUIState fieldUIState = new FilterSelectorUIState(group, field);
      filterSelectors.add(fieldUIState);
      filterSelectorsById.put(fieldUIState.getId(), fieldUIState);
      group.addFilterSelector(fieldUIState);
    }
  }

  DynamicFilterConfigMode getFilterConfigMode() {
    return filterConfigMode;
  }

  FilterFieldUIState createFilter(String filterSelectorId) {
    FilterSelectorUIState filterSelector = filterSelectorsById.get(filterSelectorId);
    FilterGroupUIState group;
    if (filterConfigMode == DynamicFilterConfigMode.SIMPLE_DYNAMIC
        || filterConfigMode == DynamicFilterConfigMode.STATIC) {
      group = filterSelector.getGroup().currentGroupUIState;
      if (group == null) {
        group = createFilterGroup(filterSelector.getGroup(), rootGroup, false);
        filterSelector.getGroup().currentGroupUIState = group;
      }
    } else {
      group = activeGroup;
    }
    DynamicFilter filter = new DynamicFilter();
    filter.setMetaName(filterSelector.getName());
    group.addDynamicFilter(filter);
    List<DynamicFilterOperation> options = filterSelector.getOperations();
    // TODO default operation handling
    if (options != null && !options.isEmpty()) {
      filter.setOperation(options.get(0));
    }
    DynamicFilterLabelPosition position = DynamicFilterLabelPosition.ON_TOP; // default
    if (filterConfigMode == DynamicFilterConfigMode.STATIC) {
      position = DynamicFilterLabelPosition.PLACEHOLDER;
    }
    boolean isCloseable = !(filterConfigMode == DynamicFilterConfigMode.STATIC);
    FilterFieldUIState filterUIState =
        new FilterFieldUIState(filter, group, position, isCloseable);
    filterUIStatesById.put(filterUIState.getId(), filterUIState);
    filtersById.put(filterUIState.getId(), filter);
    return filterUIState;
  }

  FilterGroupUIState createFilterGroup(FilterSelectorGroupUIState groupMeta,
      FilterGroupUIState parentGroup, boolean isCloseable) {
    DynamicFilterGroup group = new DynamicFilterGroup();
    group.setType(groupMeta.getType());
    group.setName(groupMeta.getName());
    group.setIcon(groupMeta.getIconCode());
    FilterGroupUIState groupUIState = new FilterGroupUIState(group, parentGroup, isCloseable);
    groupUIStatesById.put(groupUIState.getId(), groupUIState);
    groupsById.put(groupUIState.getId(), group);
    return groupUIState;
  }
}