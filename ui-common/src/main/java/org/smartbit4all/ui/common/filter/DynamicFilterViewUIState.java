/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
  private FilterGroup rootFilterGroup;

  public FilterGroup getRootFilterGroup() {
    return rootFilterGroup;
  }

  public DynamicFilterViewUIState(FilterConfigMode filterConfigMode) {
    this.filterConfigMode = filterConfigMode;
    if (this.filterConfigMode == null) {
      this.filterConfigMode = FilterConfigMode.SIMPLE_DYNAMIC;
    }
    createRootGroup();
  }

  private void createRootGroup() {
    rootFilterGroup = new FilterGroup();
    rootFilterGroup.setType(FilterGroupType.AND);
    rootFilterGroup.setName("filter.root");
    boolean isRootVisible = filterConfigMode == FilterConfigMode.DYNAMIC;
    boolean isChildGroupAllowed = filterConfigMode == FilterConfigMode.DYNAMIC;
    boolean isGroupTypeChangeEnabled = filterConfigMode == FilterConfigMode.DYNAMIC;

    rootGroup = new FilterGroupUIState(rootFilterGroup, null, null, false, isRootVisible,
        isChildGroupAllowed, isGroupTypeChangeEnabled);
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
      groupsByName.put(group.getLabelCode(), groupUIState);
      for (FilterFieldMeta field : group.getFilterFieldMetas()) {
        FilterSelectorUIState fieldUIState = new FilterSelectorUIState(groupUIState, field);
        filterSelectors.add(fieldUIState);
        filterSelectorsById.put(fieldUIState.getId(), fieldUIState);
        groupUIState.addFilterSelector(fieldUIState);
      }
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
    group.addFilterField(filter);
    List<FilterOperation> operations = filterSelector.getOperations();
    FilterLabelPosition position = FilterLabelPosition.ON_TOP; // default
    if (filterConfigMode == FilterConfigMode.STATIC) {
      position = FilterLabelPosition.PLACEHOLDER;
    }
    boolean isCloseable = !(filterConfigMode == FilterConfigMode.STATIC);
    FilterFieldUIState filterUIState =
        new FilterFieldUIState(filter, filterSelector, group, position, isCloseable);
    // TODO default operation handling
    if (operations != null && !operations.isEmpty()) {
      filterUIState.setSelectedOperation(operations.get(0));
    }
    filterUIStatesById.put(filterUIState.getId(), filterUIState);
    filtersById.put(filterUIState.getId(), filter);
    return filterUIState;
  }

  FilterGroupUIState createFilterGroup(FilterGroupUIState parentGroup, String name, String iconCode,
      FilterGroupType type) {
    FilterGroup group = new FilterGroup();
    group.setName(name);
    group.setType(type);
    parentGroup.addFilterGroup(group);
    boolean isCloseable = filterConfigMode == FilterConfigMode.DYNAMIC;
    boolean isChildGroupAllowed = filterConfigMode == FilterConfigMode.DYNAMIC;
    boolean isGroupTypeChangeEnabled = filterConfigMode == FilterConfigMode.DYNAMIC;

    FilterGroupUIState groupUIState =
        new FilterGroupUIState(group, parentGroup, iconCode, isCloseable, true,
            isChildGroupAllowed, isGroupTypeChangeEnabled);
    groupUIStatesById.put(groupUIState.getId(), groupUIState);
    groupsById.put(groupUIState.getId(), group);
    return groupUIState;
  }

  FilterGroupUIState createFilterGroup(FilterSelectorGroupUIState groupMeta,
      FilterGroupUIState parentGroup) {
    return createFilterGroup(parentGroup, groupMeta.getLabelCode(), groupMeta.getIconCode(),
        groupMeta.getType());
  }

  FilterGroupUIState removeFilterGroup(String groupId) {
    FilterGroup group = groupsById.get(groupId);
    groupsById.remove(groupId);
    FilterGroupUIState groupUIState = groupUIStatesById.get(groupId);
    groupUIState.clearChildren();
    FilterGroupUIState parentGroup = groupUIState.getParentGroup();
    if (parentGroup != null) {
      parentGroup.removeFilterGroup(group);
      if (groupUIState.isActive()) {
        setActiveGroup(parentGroup.getId());
      }
    }
    for (FilterSelectorGroupUIState selectorGroup : filterSelectorGroups) {
      if (selectorGroup.currentGroupUIState == groupUIState) {
        selectorGroup.currentGroupUIState = null;
        break;
      }
    }
    groupUIStatesById.remove(groupId);
    return parentGroup;
  }

  String getActiveGroupId() {
    return activeGroup.getId();
  }

  String setActiveGroup(String groupId) {
    activeGroup.setActive(false);
    String prevActiveGroup = activeGroup.getId();
    activeGroup = groupUIStatesById.get(groupId);
    activeGroup.setActive(true);
    return prevActiveGroup;
  }

  void moveFilter(String oldGroupId, String newGroupId, String filterId) {
    FilterGroup oldFilterGroup = groupsById.get(oldGroupId);
    FilterGroup newFilterGroup = groupsById.get(newGroupId);
    FilterField filterField = filtersById.get(filterId);

    oldFilterGroup.getFilterFields().remove(filterField);
    newFilterGroup.addFilterFieldsItem(filterField);

    FilterFieldUIState filterUiState = filterUIStatesById.get(filterId);
    FilterGroupUIState newGroup = groupUIStatesById.get(newGroupId);
    filterUiState.setGroup(newGroup);


  }

}
