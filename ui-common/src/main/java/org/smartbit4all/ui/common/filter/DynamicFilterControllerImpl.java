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

import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.filter.FilterApi;
import org.smartbit4all.api.filter.bean.FilterConfig;
import org.smartbit4all.api.filter.bean.FilterConfigMode;
import org.smartbit4all.api.filter.bean.FilterField;
import org.smartbit4all.api.filter.bean.FilterGroup;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.api.value.ValueApi;
import org.smartbit4all.api.value.bean.Value;

public class DynamicFilterControllerImpl implements DynamicFilterController {

  private static final Logger log = LoggerFactory.getLogger(DynamicFilterController.class);

  private FilterApi api;
  private DynamicFilterView ui;
  private String uri;
  private ValueApi valueApi;

  private DynamicFilterViewUIState uiState;

  public DynamicFilterControllerImpl(FilterApi api, String uri,
      FilterConfigMode filterConfigMode, ValueApi valueApi) {
    this.api = api;
    this.uri = uri;
    this.valueApi = valueApi;
    this.uiState = new DynamicFilterViewUIState(filterConfigMode);

  }

  @Override
  public void setUi(DynamicFilterView dynamicFilterView) {
    this.ui = dynamicFilterView;
    // visible / not visible?
    ui.renderGroup(uiState.rootGroup);
  }

  @Override
  public void loadData() {
    FilterConfig filterConfig = api.getFilterConfig(uri);
    uiState.setFilterConfig(filterConfig);
    switch (uiState.getFilterConfigMode()) {
      case STATIC:
        renderStaticFilterConfig(filterConfig);
        break;
      case SIMPLE_DYNAMIC:
        renderSimpleFilterConfig(filterConfig);
        break;
      case DYNAMIC:
        renderDynamicFilterConfig(filterConfig);
        break;
    }
  }

  private void renderStaticFilterConfig(FilterConfig filterConfig) {
    // no filterSelector
    for (FilterSelectorUIState selector : uiState.filterSelectors) {
      selector.setEnabled(false);
      addFilterField(selector.getId());
    }
  }

  private void renderSimpleFilterConfig(FilterConfig filterConfig) {
    ui.renderFilterSelectors(uiState.filterSelectorGroups);
  }

  private void renderDynamicFilterConfig(FilterConfig filterConfig) {
    ui.renderFilterSelectors(uiState.filterSelectorGroups);
  }

  @Override
  public void addFilterField(String filterSelectorId) {
    FilterFieldUIState filterUIState = uiState.createFilter(filterSelectorId);
    if (uiState.getFilterConfigMode() == FilterConfigMode.SIMPLE_DYNAMIC) {
      FilterSelectorUIState filterSelector = uiState.filterSelectorsById.get(filterSelectorId);
      filterSelector.setEnabled(false);
      ui.updateFilterSelector(filterSelector);
    }
    if (uiState.getFilterConfigMode() == FilterConfigMode.DYNAMIC) {
      filterUIState.setDraggable(true);
    }
    filterUIState.setPossibleValues(getPossibleValues(filterUIState));
    ui.renderFilter(filterUIState);
    logRootFilterGroupState();
  }

  private List<Value> getPossibleValues(FilterFieldUIState filterUIState) {
    URI uri = filterUIState.getSelectedOperation().getPossibleValuesUri();

    if (uri == null) {
      return null;
    }

    List<Value> possibleValues;
    try {
      possibleValues = valueApi.getPossibleValues(uri);
    } catch (Exception e) {
      // TODO handle this better
      throw new RuntimeException(e);
    }
    return possibleValues;
  }

  @Override
  public void filterOperationChanged(String filterId, String filterOperationId) {
    if (filterOperationId == null) {
      throw new NullPointerException("FilterOperation cannot be null!");
    }
    FilterFieldUIState filterUIState = uiState.filterUIStatesById.get(filterId);

    FilterField filter = filterUIState.getFilter();
    for (FilterOperation operation : filterUIState.getOperations()) {
      if (filterOperationId.equals(operation.getId())) {
        if (filterUIState.getSelectedOperation() == operation) {
          // no change
          return;
        }
        filterUIState.setSelectedOperation(operation);
        logRootFilterGroupState();
        ui.renderFilter(filterUIState);
        return;
      }
    }
    throw new RuntimeException("No filterOperation found by code " + filterOperationId + "!");
  }

  @Override
  public void filterValueChanged(String filterId, FilterOperandValue value1,
      FilterOperandValue value2, FilterOperandValue value3) {
    FilterFieldUIState filterFieldState = uiState.filterUIStatesById.get(filterId);

    filterFieldState.getFilter().setValue1(value1);
    filterFieldState.getFilter().setValue2(value2);
    filterFieldState.getFilter().setValue3(value3);
    ui.updateFilterState(filterFieldState);
    logRootFilterGroupState();
  }

  @Override
  public void filterSelectionChanged(String filterId, List<URI> values) {
    FilterFieldUIState filterFieldState = uiState.filterUIStatesById.get(filterId);

    filterFieldState.getFilter().setSelectedValues(values);
    ui.updateFilterState(filterFieldState);
    logRootFilterGroupState();
  }

  @Override
  public void removeFilterField(String groupId, String filterId) {
    // TODO move to uiState.removeFilter()
    FilterGroup group = uiState.groupsById.get(groupId);
    FilterField filter = uiState.filtersById.get(filterId);
    uiState.filtersById.remove(filterId);
    ui.removeFilter(filterId);
    group.getFilterFields().remove(filter);
    if (uiState.getFilterConfigMode() == FilterConfigMode.SIMPLE_DYNAMIC) {
      // find selector
      FilterFieldUIState filterUIState = uiState.filterUIStatesById.get(filterId);
      FilterSelectorUIState selector =
          uiState.filterSelectorsById.get(filterUIState.getSelectorId());
      selector.setEnabled(true);
      ui.updateFilterSelector(selector);
      if (group.getFilterFields().isEmpty()) {
        removeFilterGroup(groupId);
      }
    }
    logRootFilterGroupState();
  }

  @Override
  public void removeFilterGroup(String groupId) {
    ui.removeGroup(groupId);
    FilterGroupUIState filterGroup = uiState.removeFilterGroup(groupId);
    ui.changeActiveGroup(filterGroup, filterGroup);
    logRootFilterGroupState();
  }

  @Override
  public void activeFilterGroupChanged(String filterGroupId) {
    String previousGroupId = uiState.setActiveGroup(filterGroupId);
    FilterGroupUIState previousFilterGroupUIState = uiState.groupUIStatesById.get(previousGroupId);
    FilterGroupUIState newFilterGroupUIState = uiState.groupUIStatesById.get(filterGroupId);
    ui.changeActiveGroup(previousFilterGroupUIState, newFilterGroupUIState);

  }

  @Override
  public void addFilterGroup(String parentGroupId) {
    FilterGroupUIState parentGroup = uiState.groupUIStatesById.get(parentGroupId);
    FilterGroupUIState filterGroup =
        uiState.createFilterGroup(parentGroup, null, null, FilterGroupType.AND);
    ui.renderGroup(filterGroup);
    activeFilterGroupChanged(filterGroup.getId());
    logRootFilterGroupState();
  }

  @Override
  public void changeGroup(String oldGroupId, String newGroupId, String filterId) {
    ui.moveFilter(newGroupId, filterId);
    uiState.moveFilter(oldGroupId, newGroupId, filterId);
    logRootFilterGroupState();
  }

  @Override
  public void changeFilterGroupType(String filterGroupId, FilterGroupType type) {
    uiState.groupsById.get(filterGroupId).setType(type);
    ui.changeFilterGroupType(filterGroupId, type);
    logRootFilterGroupState();
  }

  @Override
  public FilterGroup getRootFilterGroup() {
    return uiState.getRootFilterGroup();
  }

  protected void logRootFilterGroupState() {
    if (log.isDebugEnabled()) {
      log.debug(uiState.getRootFilterGroup().toString());
    }
  }
}
