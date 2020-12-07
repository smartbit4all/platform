package org.smartbit4all.ui.common.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.filter.bean.FilterConfig;
import org.smartbit4all.api.filter.bean.FilterConfigMode;
import org.smartbit4all.api.filter.bean.FilterField;
import org.smartbit4all.api.filter.bean.FilterFieldMeta;
import org.smartbit4all.api.filter.bean.FilterGroup;
import org.smartbit4all.api.filter.bean.FilterGroupMeta;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.api.filter.bean.FilterOperation;

public class DynamicFilterViewUIState {

  List<FilterSelectorGroupUIState> filterSelectorGroups = new ArrayList<>();
  List<FilterSelectorUIState> filterSelectors = new ArrayList<>();
  FilterGroupUIState rootGroup;
  Map<String, FilterSelectorGroupUIState> filterSelectorGroupsById = new HashMap<>();
  Map<String, FilterSelectorUIState> filterSelectorsById = new HashMap<>();
  Map<String, FilterGroupUIState> groupUIStatesById = new HashMap<>();
  Map<String, FilterFieldUIState> filterUIStatesById = new HashMap<>();
  Map<String, FilterGroup> groupsById = new HashMap<>();
  Map<String, FilterField> filtersById = new HashMap<>();
  FilterGroupUIState activeGroup;

  private FilterConfig filterConfig;
  private FilterConfigMode filterConfigMode;

  public DynamicFilterViewUIState(FilterConfigMode filterConfigMode) {
    this.filterConfigMode = filterConfigMode;
    if (this.filterConfigMode == null) {
      this.filterConfigMode = FilterConfigMode.SIMPLE_DYNAMIC;
    }
    createRootGroup();
  }

  private void createRootGroup() {
    FilterGroup rootFilterGroup = new FilterGroup();
    rootFilterGroup.setType(FilterGroupType.AND);
    rootFilterGroup.setName("filter.root");
    boolean isRootVisible = filterConfigMode == FilterConfigMode.DYNAMIC;
    rootGroup = new FilterGroupUIState(rootFilterGroup, null, null, false, isRootVisible);
    activeGroup = rootGroup;
    groupUIStatesById.put(rootGroup.getId(), rootGroup);
    groupsById.put(rootGroup.getId(), rootFilterGroup);
  }

  void setFilterConfig(FilterConfig filterConfig) {
    this.filterConfig = filterConfig;
    Map<String, FilterSelectorGroupUIState> groupsByName = new HashMap<>();
    for (FilterGroupMeta group : this.filterConfig.getFilterGroupMetas()) {
      FilterSelectorGroupUIState groupUIState = new FilterSelectorGroupUIState(group);
      filterSelectorGroups.add(groupUIState);
      filterSelectorGroupsById.put(groupUIState.getId(), groupUIState);
      groupsByName.put(group.getName(), groupUIState);
    }
    for (FilterFieldMeta field : this.filterConfig.getFilterFieldMetas()) {
      FilterSelectorGroupUIState group = groupsByName.get(field.getGroupName());
      FilterSelectorUIState fieldUIState = new FilterSelectorUIState(group, field);
      filterSelectors.add(fieldUIState);
      filterSelectorsById.put(fieldUIState.getId(), fieldUIState);
      group.addFilterSelector(fieldUIState);
    }
  }

  FilterConfigMode getFilterConfigMode() {
    return filterConfigMode;
  }

  FilterFieldUIState createFilter(String filterSelectorId) {
    FilterSelectorUIState filterSelector = filterSelectorsById.get(filterSelectorId);
    FilterGroupUIState group;
    if (filterConfigMode == FilterConfigMode.SIMPLE_DYNAMIC
        || filterConfigMode == FilterConfigMode.STATIC) {
      group = filterSelector.getGroup().currentGroupUIState;
      if (group == null) {
        group = createFilterGroup(filterSelector.getGroup(), rootGroup);
        filterSelector.getGroup().currentGroupUIState = group;
      }
    } else {
      group = activeGroup;
    }
    FilterField filter = new FilterField();
    filter.setMetaName(filterSelector.getName());
    group.addDynamicFilter(filter);
    List<FilterOperation> operations = filterSelector.getOperations();
    // TODO default operation handling
    if (operations != null && !operations.isEmpty()) {
      filter.setOperation(operations.get(0));
    }
    FilterLabelPosition position = FilterLabelPosition.ON_TOP; // default
    if (filterConfigMode == FilterConfigMode.STATIC) {
      position = FilterLabelPosition.PLACEHOLDER;
    }
    boolean isCloseable = !(filterConfigMode == FilterConfigMode.STATIC);
    FilterFieldUIState filterUIState =
        new FilterFieldUIState(filter, group, position, isCloseable, operations);
    filterUIStatesById.put(filterUIState.getId(), filterUIState);
    filtersById.put(filterUIState.getId(), filter);
    return filterUIState;
  }

  FilterGroupUIState createFilterGroup(FilterSelectorGroupUIState groupMeta,
      FilterGroupUIState parentGroup) {
    FilterGroup group = new FilterGroup();
    group.setType(groupMeta.getType());
    group.setName(groupMeta.getName());
    boolean isCloseable = filterConfigMode == FilterConfigMode.DYNAMIC;
    FilterGroupUIState groupUIState =
        new FilterGroupUIState(group, parentGroup, groupMeta.getIconCode(), isCloseable, true);
    groupUIStatesById.put(groupUIState.getId(), groupUIState);
    groupsById.put(groupUIState.getId(), group);
    return groupUIState;
  }

  void removeFilterGroup(String groupId) {
    groupsById.remove(groupId);
    FilterGroupUIState groupUIState = groupUIStatesById.get(groupId);
    for (FilterSelectorGroupUIState selectorGroup : filterSelectorGroups) {
      if (selectorGroup.currentGroupUIState == groupUIState) {
        selectorGroup.currentGroupUIState = null;
        break;
      }
    }
    groupUIStatesById.remove(groupId);
  }

}
