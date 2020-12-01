package org.smartbit4all.ui.common.filter;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfig;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfigMode;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.filter.DynamicFilterApi;
import org.smartbit4all.api.value.ValueApi;
import org.smartbit4all.api.value.bean.Value;

public class DynamicFilterControllerImpl implements DynamicFilterController {

  private DynamicFilterApi api;
  private DynamicFilterView ui;
  private String uri;
  private ValueApi valueApi;

  private DynamicFilterViewUIState uiState;

  public DynamicFilterControllerImpl(DynamicFilterApi api, String uri,
      DynamicFilterConfigMode filterConfigMode, ValueApi valueApi) {
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
    DynamicFilterConfig filterConfig = api.getFilterConfig(uri);
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
      case RESEARCH:
        renderResearchFilterConfig(filterConfig);
        break;
    }
  }

  private void renderStaticFilterConfig(DynamicFilterConfig filterConfig) {
    // no filterSelector
    for (FilterSelectorUIState selector : uiState.filterSelectors) {
      addFilter(selector.getId());
    }
  }

  private void renderSimpleFilterConfig(DynamicFilterConfig filterConfig) {
    ui.renderFilterSelectors(uiState.filterSelectorGroups);
  }

  private void renderDynamicFilterConfig(DynamicFilterConfig filterConfig) {
    ui.renderFilterSelectors(uiState.filterSelectorGroups);
  }

  private void renderResearchFilterConfig(DynamicFilterConfig filterConfig) {
    // TODO
  }

  @Override
  public void addFilter(String filterSelectorId) {
    FilterFieldUIState filterUIState = uiState.createFilter(filterSelectorId);
    ui.renderFilter(filterUIState, getPossibleValues(filterUIState.getFilter()));
  }

  private List<Value> getPossibleValues(DynamicFilter dynamicFilter) {
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
  public void filterOptionChanged(String filterId, int filterOptionIdx) {
    // String descName = descriptorNameByFilterId.get(filterId);
    // DynamicFilterMeta descriptor = filterMetaByName.get(descName);
    // DynamicFilter dynamicFilter = filtersById.get(filterId);
    // List<DynamicFilterOperation> filterOptions = descriptor.getOperations();
    // if (filterOptions == null || filterOptions.size() <= filterOptionIdx) {
    // throw new IllegalArgumentException("Invalid filter option index!");
    // }
    // DynamicFilterOperation filterOption = filterOptions.get(filterOptionIdx);
    // dynamicFilter.setOperation(filterOption);
    // List<Value> possibleValues = getPossibleValues(dynamicFilter);
    // ui.renderFilter(filterId, dynamicFilter, possibleValues);
  }

  public void filterValueChanged(String filterId, String... values) {
    // TODO
  }

  // TODO filterSelectionChanged

  protected String createIdentifier() {
    return UUID.randomUUID().toString();
  }

  @Override
  public void removeFilter(String groupId, String filterId) {
    DynamicFilterGroup group = uiState.groupsById.get(groupId);
    DynamicFilter filter = uiState.filtersById.get(filterId);
    uiState.filtersById.remove(filterId);
    ui.removeFilter(filterId);
    group.getFilters().remove(filter);
    if (group.getFilters().isEmpty()) {
      removeGroup(groupId);
    }
  }

  @Override
  public void removeGroup(String groupId) {
    ui.removeGroup(groupId);
    uiState.removeFilterGroup(groupId);
  }

}
