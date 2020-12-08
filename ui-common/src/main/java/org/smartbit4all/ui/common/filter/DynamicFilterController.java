package org.smartbit4all.ui.common.filter;

import org.smartbit4all.ui.common.controller.UIController;

public interface DynamicFilterController extends UIController {

  public static final String ROOT_FILTER_GROUP = "root";

  void setUi(DynamicFilterView dynamicFilterView);

  void loadData();

  void addFilter(String filterSelectorId);

  void filterOptionChanged(String filterId, String filterOperation);

  void removeFilter(String groupId, String filterId);

  void removeGroup(String groupId);

  void activeFilterGroupChanged(String filterGroupId);

  void addSubGroup(String parentGroupId);

}
