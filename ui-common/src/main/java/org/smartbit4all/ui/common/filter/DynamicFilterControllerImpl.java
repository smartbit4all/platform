package org.smartbit4all.ui.common.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfig;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterDescriptor;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupDescriptor;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupType;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOperation;
import org.smartbit4all.api.filter.DynamicFilterApi;

public class DynamicFilterControllerImpl implements DynamicFilterController {

  private DynamicFilterApi api;
  private DynamicFilterView ui;
  private String uri;

  private DynamicFilterGroup rootFilterGroup;

  private Map<String, DynamicFilterDescriptor> filterDescriptorsByName = new HashMap<>();
  private Map<String, DynamicFilterGroupDescriptor> filterGroupDescriptorsByName = new HashMap<>();
  private Map<String, DynamicFilter> filtersById = new HashMap<>();
  private Map<String, DynamicFilterGroup> groupsById = new HashMap<>();
  private Map<String, String> descriptorNameByFilterId = new HashMap<>();
  private Map<String, DynamicFilterGroup> groupsByDescriptorName = new HashMap<>();

  public DynamicFilterControllerImpl(DynamicFilterApi api, String uri) {
    this.api = api;
    this.uri = uri;
    rootFilterGroup = new DynamicFilterGroup();
    groupsById.put(ROOT_FILTER_GROUP, rootFilterGroup);

  }

  @Override
  public void setUi(DynamicFilterView dynamicFilterView) {
    this.ui = dynamicFilterView;

  }

  @Override
  public void loadData() {
    DynamicFilterConfig filterConfig = api.getFilterConfig(uri);
    filterConfig.getDynamicFilterDescriptors()
        .forEach(filter -> filterDescriptorsByName.put(filter.getName(), filter));
    filterConfig.getDynamicFilterGroupDescriptors()
        .forEach(group -> filterGroupDescriptorsByName.put(group.getName(), group));
    ui.renderFilterConfig(filterConfig);
  }

  @Override
  public void addFilter(String groupName, String descriptorName) {
    DynamicFilterGroupDescriptor groupDescriptor = filterGroupDescriptorsByName.get(groupName);
    DynamicFilterGroup filterGroup = groupsByDescriptorName.get(groupDescriptor.getName());
    if (filterGroup == null) {
      String filterGroupId = addFilterGroup(ROOT_FILTER_GROUP, groupDescriptor.getType());
      filterGroup = groupsById.get(filterGroupId);
      filterGroup.setName(groupDescriptor.getName());
      groupsByDescriptorName.put(groupDescriptor.getName(), filterGroup);
    }
    DynamicFilterDescriptor descriptor = filterDescriptorsByName.get(descriptorName);
    List<DynamicFilterOperation> filterOptions = descriptor.getOperations();

    DynamicFilter dynamicFilter = createFilter(descriptor);
    String filterId = createIdentifier();
    filterGroup.addFiltersItem(dynamicFilter);
    // TODO clear up hack&slash
    DynamicFilterGroup groupToSearch = filterGroup;
    String groupId = groupsById.entrySet().stream()
        .filter(entry -> entry.getValue().equals(groupToSearch)).findFirst().get().getKey();
    updateControllerModel(groupId, descriptorName, dynamicFilter, filterId);

    ui.renderFilter(groupId, filterId, dynamicFilter, filterOptions);
  }

  protected void updateControllerModel(String groupId, String descriptorName,
      DynamicFilter dynamicFilter,
      String filterId) {
    filtersById.put(filterId, dynamicFilter);
    descriptorNameByFilterId.put(filterId, descriptorName);
  }

  private DynamicFilter createFilter(DynamicFilterDescriptor descriptor) {
    DynamicFilter filter = new DynamicFilter();
    filter.setDescriptorName(descriptor.getName());
    List<DynamicFilterOperation> options = descriptor.getOperations();
    if (options != null && !options.isEmpty()) {
      filter.setOperation(options.get(0));
    }
    return filter;
  }

  @Override
  public String addFilterGroup(String parentGroupId, DynamicFilterGroupType groupType) {
    DynamicFilterGroup parentGroup = groupsById.get(parentGroupId);
    DynamicFilterGroup childGroup = new DynamicFilterGroup();
    childGroup.setType(groupType);
    parentGroup.addGroupsItem(childGroup);
    String childGroupId = createIdentifier();
    groupsById.put(childGroupId, childGroup);
    ui.renderGroup(parentGroupId, childGroupId, childGroup);
    return childGroupId;
  }

  // TODO removeFilterGroup
  // TODO removeFilter

  @Override
  public void filterOptionChanged(String filterId, int filterOptionIdx) {
    String descName = descriptorNameByFilterId.get(filterId);
    DynamicFilterDescriptor descriptor = filterDescriptorsByName.get(descName);
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
  public void removeFilter(String filterId) {
    filtersById.remove(filterId);
    ui.removeFilter(filterId);
  }

}
