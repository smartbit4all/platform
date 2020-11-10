package org.smartbit4all.ui.common.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterDescriptor;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupType;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOption;
import org.smartbit4all.api.filter.DynamicFilterApi;

public class DynamicFilterControllerImpl implements DynamicFilterController {

  private DynamicFilterApi api;
  private DynamicFilterView ui;
  private String uri;
  
  private DynamicFilterGroup rootFilterGroup;
  
  private Map<String, DynamicFilterDescriptor> descriptorsByName = new HashMap<>();
  private Map<String, DynamicFilter> filtersById = new HashMap<>();
  private Map<String, DynamicFilterGroup> groupsById = new HashMap<>();
  private Map<String, String> descriptorNameByFilterId = new HashMap<>();
  
  
  public DynamicFilterControllerImpl(DynamicFilterApi api, String uri) {
    this.api = api;
    this.uri = uri;
    rootFilterGroup = new DynamicFilterGroup();
    groupsById.put(ROOT_FILTER_GROUP, rootFilterGroup);
    
  }
  
  @Override
  public void setUi(DynamicFilterView dynamicFilterView) {
    this.ui = dynamicFilterView;
    loadData(); // TODO self.loadData
    
  }

  @Override
  public void loadData() {
    List<DynamicFilterDescriptor> filterConfig = api.getFilterConfig(uri);
    filterConfig.forEach( dfd -> descriptorsByName.put(dfd.getName(), dfd));
    ui.renderFilterConfig(filterConfig);
  }
  
  @Override
  public void addFilter(String groupId, String descriptorName) {
    DynamicFilterGroup parentGroup = groupsById.get(groupId);
    DynamicFilterDescriptor descriptor = descriptorsByName.get(descriptorName);
    List<DynamicFilterOption> filterOptions = descriptor.getOptions();
    
    DynamicFilter dynamicFilter = createFilter(descriptor);
    String filterId = createIdentifier(); 
    parentGroup.addFiltersItem(dynamicFilter);
    
    updateControllerModel(groupId, descriptorName, dynamicFilter, filterId);
    
    ui.renderFilter(groupId, filterId, dynamicFilter, filterOptions);
  }

  protected void updateControllerModel(String groupId, String descriptorName, DynamicFilter dynamicFilter,
      String filterId) {
    filtersById.put(filterId, dynamicFilter);
    descriptorNameByFilterId.put(filterId, descriptorName);
  }
  
  private DynamicFilter createFilter(DynamicFilterDescriptor descriptor) {
    DynamicFilter filter = new DynamicFilter();
    List<DynamicFilterOption> options = descriptor.getOptions();
    if(options != null && !options.isEmpty()) {
      filter.setOption(options.get(0));
    }
    return filter;
  }

  @Override
  public void addFilterGroup(String parentGroupId, DynamicFilterGroupType groupType) {
    DynamicFilterGroup parentGroup = groupsById.get(parentGroupId);
    DynamicFilterGroup childGroup = new DynamicFilterGroup();
    childGroup.setType(groupType);
    parentGroup.addGroupsItem(childGroup);
    String childGroupId = createIdentifier();
    groupsById.put(childGroupId, childGroup);
    ui.renderGroup(parentGroupId, childGroupId, childGroup);
  }
  
  // TODO removeFilterGroup
  // TODO removeFilter
  
  @Override
  public void filterOptionChanged(String filterId, int filterOptionIdx) {
    String descName = descriptorNameByFilterId.get(filterId);
    DynamicFilterDescriptor descriptor = descriptorsByName.get(descName);
    DynamicFilter dynamicFilter = filtersById.get(filterId);
    List<DynamicFilterOption> filterOptions = descriptor.getOptions();
    if(filterOptions == null || filterOptions.size() <= filterOptionIdx) {
      throw new IllegalArgumentException("Invalid filter option index!");
    }
    DynamicFilterOption filterOption = filterOptions.get(filterOptionIdx);
    dynamicFilter.setOption(filterOption);
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
  
}
