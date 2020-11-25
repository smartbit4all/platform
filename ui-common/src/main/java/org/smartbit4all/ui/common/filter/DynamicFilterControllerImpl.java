package org.smartbit4all.ui.common.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfig;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfigMode;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupMeta;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupType;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterMeta;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOperation;
import org.smartbit4all.api.filter.DynamicFilterApi;

public class DynamicFilterControllerImpl implements DynamicFilterController {

  private DynamicFilterApi api;
  private DynamicFilterView ui;
  private String uri;

  private DynamicFilterGroup rootFilterGroup;

  private Map<String, DynamicFilterMeta> filterMetaByName = new HashMap<>();
  private Map<String, DynamicFilterGroupMeta> filterGroupMetaByName = new HashMap<>();
  private Map<String, DynamicFilter> filtersById = new HashMap<>();
  private Map<String, DynamicFilterGroup> groupsById = new HashMap<>();
  private Map<String, String> descriptorNameByFilterId = new HashMap<>();
  private Map<String, DynamicFilterGroup> groupsByName = new HashMap<>();

  private Map<String, FilterSelectorGroupUIState> filterSelectorGroupStatesById = new HashMap<>();
  private Map<String, FilterSelectorUIState> filterSelectorStatesById = new HashMap<>();
  private Map<String, FilterGroupUIState> filterGroupStatesById = new HashMap<>();
  private Map<String, FilterFieldUIState> filterFieldStatesById = new HashMap<>();

  private DynamicFilterViewUIState uiState;

  public DynamicFilterControllerImpl(DynamicFilterApi api, String uri,
      DynamicFilterConfigMode filterConfigMode) {
    this.api = api;
    this.uri = uri;
    rootFilterGroup = new DynamicFilterGroup();
    this.uiState = new DynamicFilterViewUIState(filterConfigMode, rootFilterGroup);
    groupsById.put(ROOT_FILTER_GROUP, rootFilterGroup);

  }

  @Override
  public void setUi(DynamicFilterView dynamicFilterView) {
    this.ui = dynamicFilterView;
  }

  @Override
  public void loadData() {
    DynamicFilterConfig filterConfig = api.getFilterConfig(uri);
    uiState.setFilterConfig(filterConfig);
    filterConfig.getDynamicFilterMetas()
        .forEach(filter -> filterMetaByName.put(filter.getName(), filter));
    filterConfig.getDynamicFilterGroupMetas()
        .forEach(group -> filterGroupMetaByName.put(group.getName(), group));
    if (filterConfigMode == null) {
      filterConfigMode = DynamicFilterConfigMode.SIMPLE_DYNAMIC;
    }
    switch (filterConfigMode) {
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
    for (DynamicFilterGroupMeta group : filterConfig.getDynamicFilterGroupMetas()) {
      addFilterGroup(ROOT_FILTER_GROUP, group.getName(), group.getIcon(), group.getType(), false);
    }
    for (DynamicFilterMeta filter : filterConfig.getDynamicFilterMetas()) {
      addFilter(filter.getGroupName(), filter.getName(), false);
    }
  }

  private void renderSimpleFilterConfig(DynamicFilterConfig filterConfig) {
    ui.renderFilterSelectors(filterConfig);
  }

  private void renderDynamicFilterConfig(DynamicFilterConfig filterConfig) {
    ui.renderFilterSelectors(filterConfig);
  }

  private void renderResearchFilterConfig(DynamicFilterConfig filterConfig) {

  }

  @Override
  public void addFilter(String filterSelectorId) {
    FilterSelectorUIState filterSelector = uiState.getFilterSelectorById(filterSelectorId);
    FilterSelectorGroupUIState filterSelectorGroup = filterSelector.getGroup();

    DynamicFilterGroup filterGroup = groupsByName.get(filterSelectorGroup.getName());
    if (filterGroup == null) {
      // TODO filterGroup = addFilterGroup(ROOT_FILTER_GROUP,filterSelectorGroup);
      String filterGroupId = addFilterGroup(ROOT_FILTER_GROUP,
          filterSelectorGroup.getName(),
          filterSelectorGroup.getIconCode(),
          filterSelectorGroup.getType(),
          filterSelectorGroup.isCloseable());
      filterGroup = groupsById.get(filterGroupId);
    }
    DynamicFilterMeta filterMeta = filterMetaByName.get(filterMetaName);
    List<DynamicFilterOperation> filterOptions = filterMeta.getOperations();

    DynamicFilter dynamicFilter = createFilter(filterMeta);
    String filterId = createIdentifier();
    filterGroup.addFiltersItem(dynamicFilter);
    // TODO clear up hack&slash
    DynamicFilterGroup groupToSearch = filterGroup;
    String groupId = groupsById.entrySet().stream()
        .filter(entry -> entry.getValue().equals(groupToSearch)).findFirst().get().getKey();
    updateControllerModel(groupId, filterMetaName, dynamicFilter, filterId);

    ui.renderFilter(groupId, filterId, dynamicFilter, filterOptions, isClosable);
  }

  @Override
  public void addFilter(String groupName, String filterMetaName, boolean isClosable) {
    DynamicFilterGroupMeta groupMeta = filterGroupMetaByName.get(groupName);
    DynamicFilterGroup filterGroup = groupsByName.get(groupMeta.getName());
    if (filterGroup == null) {
      String filterGroupId = addFilterGroup(ROOT_FILTER_GROUP, groupMeta.getName(),
          groupMeta.getIcon(), groupMeta.getType(), isClosable);
      filterGroup = groupsById.get(filterGroupId);
    }
    DynamicFilterMeta filterMeta = filterMetaByName.get(filterMetaName);
    List<DynamicFilterOperation> filterOptions = filterMeta.getOperations();

    DynamicFilter dynamicFilter = createFilter(filterMeta);
    String filterId = createIdentifier();
    filterGroup.addFiltersItem(dynamicFilter);
    // TODO clear up hack&slash
    DynamicFilterGroup groupToSearch = filterGroup;
    String groupId = groupsById.entrySet().stream()
        .filter(entry -> entry.getValue().equals(groupToSearch)).findFirst().get().getKey();
    updateControllerModel(groupId, filterMetaName, dynamicFilter, filterId);

    ui.renderFilter(groupId, filterId, dynamicFilter, filterOptions, isClosable);
  }

  protected void updateControllerModel(String groupId, String descriptorName,
      DynamicFilter dynamicFilter,
      String filterId) {
    filtersById.put(filterId, dynamicFilter);
    descriptorNameByFilterId.put(filterId, descriptorName);
  }

  private DynamicFilter createFilter(DynamicFilterMeta descriptor) {
    DynamicFilter filter = new DynamicFilter();
    filter.setMetaName(descriptor.getName());
    List<DynamicFilterOperation> options = descriptor.getOperations();
    if (options != null && !options.isEmpty()) {
      filter.setOperation(options.get(0));
    }
    return filter;
  }

  @Override
  public String addFilterGroup(String parentGroupId, String groupName, String groupIcon,
      DynamicFilterGroupType groupType, boolean isClosable) {
    DynamicFilterGroup parentGroup = groupsById.get(parentGroupId);
    DynamicFilterGroup childGroup = new DynamicFilterGroup();
    childGroup.setType(groupType);
    childGroup.setName(groupName);
    childGroup.setIcon(groupIcon);
    parentGroup.addGroupsItem(childGroup);
    String childGroupId = createIdentifier();
    groupsById.put(childGroupId, childGroup);
    groupsByName.put(groupName, childGroup);
    ui.renderGroup(parentGroupId, childGroupId, childGroup, isClosable);
    return childGroupId;
  }

  // TODO removeFilterGroup
  // TODO removeFilter

  @Override
  public void filterOptionChanged(String filterId, int filterOptionIdx) {
    String descName = descriptorNameByFilterId.get(filterId);
    DynamicFilterMeta descriptor = filterMetaByName.get(descName);
    DynamicFilter dynamicFilter = filtersById.get(filterId);
    List<DynamicFilterOperation> filterOptions = descriptor.getOperations();
    if (filterOptions == null || filterOptions.size() <= filterOptionIdx) {
      throw new IllegalArgumentException("Invalid filter option index!");
    }
    DynamicFilterOperation filterOption = filterOptions.get(filterOptionIdx);
    dynamicFilter.setOperation(filterOption);
    ui.renderFilter(filterId, dynamicFilter);
  }

  public void filterValueChanged(String filterId, String... values) {
    // TODO
  }

  // TODO filterSelectionChanged



  @Override
  public DynamicFilterGroup getFilters() {
    return rootFilterGroup;
  }

  protected String createIdentifier() {
    return UUID.randomUUID().toString();
  }

  @Override
  public void removeFilter(String groupId, String filterId) {
    DynamicFilterGroup group = groupsById.get(groupId);
    DynamicFilter filter = filtersById.get(filterId);
    filtersById.remove(filterId);
    ui.removeFilter(filterId);
    group.getFilters().remove(filter);
    if (group.getFilters().isEmpty()) {
      removeGroup(groupId);
    }
  }

  @Override
  public void removeGroup(String groupId) {
    ui.removeGroup(groupId);
    DynamicFilterGroup group = groupsById.get(groupId);
    // FIXME DynamicFilterGroup should know it's meta id (descriptorName)
    String groupMetaName = null;
    for (Entry<String, DynamicFilterGroup> entry : groupsByName.entrySet()) {
      if (entry.getValue() == group) {
        groupMetaName = entry.getKey();
        break;
      }
    }
    if (groupMetaName != null) {
      groupsByName.remove(groupMetaName);
    }
  }

}
