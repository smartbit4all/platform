/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.common.filter;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.filter.FilterApi;
import org.smartbit4all.api.filter.bean.FilterConfig;
import org.smartbit4all.api.filter.bean.FilterConfigMode;
import org.smartbit4all.api.filter.bean.FilterField;
import org.smartbit4all.api.filter.bean.FilterGroup;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.api.value.ValueApi;
import org.smartbit4all.api.value.bean.Value;

public class DynamicFilterControllerImpl implements DynamicFilterController {

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
    filterUIState.setPossibleValues(getPossibleValues(filterUIState.getFilter()));
    ui.renderFilter(filterUIState);
    System.out.println(uiState.getRootFilterGroup().toString());
  }

  private List<Value> getPossibleValues(FilterField dynamicFilter) {
    URI uri = dynamicFilter.getOperation().getPossibleValues();

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
  public void filterOperationChanged(String filterId, String filterOperation) {
    if (filterOperation == null) {
      throw new NullPointerException("FilterOperation cannot be null!");
    }
    FilterFieldUIState filterUIState = uiState.filterUIStatesById.get(filterId);
    FilterField filter = filterUIState.getFilter();
    if (filterOperation.equals(filter.getOperation().getCode())) {
      // no change
      return;
    }
    for (FilterOperation operation : filterUIState.getOperations()) {
      if (filterOperation.equals(operation.getCode())) {
        filterUIState.getFilter().setOperation(operation);
        ui.renderFilter(filterUIState);
        return;
      }
    }
    throw new RuntimeException("No filterOperation found by code " + filterOperation + "!");
  }

  public void filterValueChanged(String filterId, String... values) {
    // TODO
  }

  // TODO filterSelectionChanged

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
      for (FilterSelectorUIState selector : uiState.filterSelectors) {
        if (selector.getName().equals(filter.getMetaName())) {
          selector.setEnabled(true);
          ui.updateFilterSelector(selector);
          break;
        }
      }
      if (group.getFilterFields().isEmpty()) {
        removeFilterGroup(groupId);
      }
    }
    System.out.println(uiState.getRootFilterGroup().toString());
  }

  @Override
  public void removeFilterGroup(String groupId) {
    ui.removeGroup(groupId);
    FilterGroupUIState filterGroup = uiState.removeFilterGroup(groupId);
    ui.changeActiveGroup(filterGroup, filterGroup);
    System.out.println(uiState.getRootFilterGroup().toString());
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
    System.out.println(uiState.getRootFilterGroup().toString());
  }

  public void changeGroup(String oldGroupId, String newGroupId, String filterId) {
    ui.moveFilter(newGroupId, filterId);
    uiState.moveFilter(oldGroupId, newGroupId, filterId);
    System.out.println(uiState.getRootFilterGroup().toString());
  }


}
