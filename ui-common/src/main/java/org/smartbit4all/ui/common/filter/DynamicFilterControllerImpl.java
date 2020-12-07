package org.smartbit4all.ui.common.filter;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.filter.FilterApi;
import org.smartbit4all.api.filter.bean.FilterConfig;
import org.smartbit4all.api.filter.bean.FilterConfigMode;
import org.smartbit4all.api.filter.bean.FilterField;
import org.smartbit4all.api.filter.bean.FilterGroup;
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
      addFilter(selector.getId());
    }
  }

  private void renderSimpleFilterConfig(FilterConfig filterConfig) {
    ui.renderFilterSelectors(uiState.filterSelectorGroups);
  }

  private void renderDynamicFilterConfig(FilterConfig filterConfig) {
    ui.renderFilterSelectors(uiState.filterSelectorGroups);
  }

  @Override
  public void addFilter(String filterSelectorId) {
    FilterFieldUIState filterUIState = uiState.createFilter(filterSelectorId);
    if (uiState.getFilterConfigMode() == FilterConfigMode.SIMPLE_DYNAMIC) {
      FilterSelectorUIState filterSelector = uiState.filterSelectorsById.get(filterSelectorId);
      filterSelector.setEnabled(false);
      ui.updateFilterSelector(filterSelector);
    }
    filterUIState.setPossibleValues(getPossibleValues(filterUIState.getFilter()));
    ui.renderFilter(filterUIState);
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
  public void filterOptionChanged(String filterId, String filterOperation) {
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
  public void removeFilter(String groupId, String filterId) {
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
        removeGroup(groupId);
      }
    }
  }

  @Override
  public void removeGroup(String groupId) {
    ui.removeGroup(groupId);
    uiState.removeFilterGroup(groupId);
  }

}
