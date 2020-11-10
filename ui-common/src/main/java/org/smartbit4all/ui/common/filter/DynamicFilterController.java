package org.smartbit4all.ui.common.filter;

import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupType;
import org.smartbit4all.ui.common.controller.UIController;

public interface DynamicFilterController extends UIController {

  public static final String ROOT_FILTER_GROUP = "root";
  
  void setUi(DynamicFilterView dynamicFilterView);

  void loadData();

  void addFilter(String groupId, String descriptorName);

  void addFilterGroup(String parentGroupId, DynamicFilterGroupType groupType);

  void filterOptionChanged(String filterId, int filterOptionIdx);

  DynamicFilterGroup getFilters();
}
